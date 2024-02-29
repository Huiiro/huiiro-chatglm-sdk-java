package com.huiiro.chatglm.sdk;

import com.huiiro.chatglm.sdk.model.chat.ChatCompletionRequest;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionResponse;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionSyncResponse;
import com.huiiro.chatglm.sdk.model.image.ImageCompletionRequest;
import com.huiiro.chatglm.sdk.model.image.ImageCompletionResponse;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IOpenAiApi {

    String v3_completions = "api/paas/v3/model-api/{model}/sse-invoke";
    String v3_completions_sync = "api/paas/v3/model-api/{model}/invoke";
    String v4 = "api/paas/v4/chat/completions";
    String cogview3 = "api/paas/v4/images/generations";

    @POST(v3_completions)
    Single<ChatCompletionResponse> completions(@Path("model") String model, @Body ChatCompletionRequest chatCompletionRequest);

    @POST(v3_completions_sync)
    Single<ChatCompletionSyncResponse> completions(@Body ChatCompletionRequest chatCompletionRequest);

    @POST(cogview3)
    Single<ImageCompletionResponse> genImages(@Body ImageCompletionRequest imageCompletionRequest);
}
