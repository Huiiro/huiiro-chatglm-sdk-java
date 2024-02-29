package com.huiiro.chatglm.sdk.executor.aigc;

import com.alibaba.fastjson.JSON;
import com.huiiro.chatglm.sdk.IOpenAiApi;
import com.huiiro.chatglm.sdk.executor.Executor;
import com.huiiro.chatglm.sdk.executor.result.ResultHandler;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionRequest;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionResponse;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionSyncResponse;
import com.huiiro.chatglm.sdk.model.common.EventType;
import com.huiiro.chatglm.sdk.model.image.ImageCompletionRequest;
import com.huiiro.chatglm.sdk.model.image.ImageCompletionResponse;
import com.huiiro.chatglm.sdk.session.Configuration;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class GLMExecutor implements ResultHandler, Executor {

    private final Configuration configuration;

    private final EventSource.Factory factory;

    private final IOpenAiApi openAiApi;

    private final OkHttpClient okHttpClient;

    public GLMExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.factory = configuration.createRequestFactory();
        this.openAiApi = configuration.getOpenAiApi();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    @Override
    public EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception {
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v4))
                .post(RequestBody.create(MediaType.parse(Configuration.JSON_CONTENT_TYPE), chatCompletionRequest.toString()))
                .build();

        return factory.newEventSource(request, chatCompletionRequest.getIsCompatible() ? eventSourceListener(eventSourceListener) : eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuffer dataBuffer = new StringBuffer();

        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v4))
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), chatCompletionRequest.toString()))
                .build();

        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                    return;
                }
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果：{}", JSON.toJSONString(response));
                List<ChatCompletionResponse.Choice> choices = response.getChoices();
                for (ChatCompletionResponse.Choice choice : choices) {
                    if (!"stop".equals(choice.getFinishReason())) {
                        dataBuffer.append(choice.getDelta().getContent());
                    }
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                future.complete(dataBuffer.toString());
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });
        return future;
    }

    @Override
    public ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception {

        chatCompletionRequest.setStream(false);

        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAiApi.v4))
                .post(RequestBody.create(MediaType.parse(Configuration.JSON_CONTENT_TYPE), chatCompletionRequest.toString()))
                .build();
        OkHttpClient okHttpClient = configuration.getOkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Request failed");
        }
        return JSON.parseObject(response.body().string(), ChatCompletionSyncResponse.class);
    }

    @Override
    public ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception {
        return openAiApi.genImages(imageCompletionRequest).blockingGet();
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    return;
                }
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                if (response.getChoices() != null && 1 == response.getChoices().size()
                        && "stop".equals(response.getChoices().get(0).getFinishReason())) {
                    eventSourceListener.onEvent(eventSource, id, EventType.finish.getCode(), data);
                    return;
                }
                eventSourceListener.onEvent(eventSource, id, EventType.add.getCode(), data);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }
        };
    }
}