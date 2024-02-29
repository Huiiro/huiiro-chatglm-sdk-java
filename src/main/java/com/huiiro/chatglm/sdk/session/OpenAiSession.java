package com.huiiro.chatglm.sdk.session;

import com.huiiro.chatglm.sdk.model.chat.ChatCompletionRequest;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionSyncResponse;
import com.huiiro.chatglm.sdk.model.image.ImageCompletionRequest;
import com.huiiro.chatglm.sdk.model.image.ImageCompletionResponse;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

import java.util.concurrent.CompletableFuture;

public interface OpenAiSession {

    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception;

    Configuration configuration();
}
