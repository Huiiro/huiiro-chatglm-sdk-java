package com.huiiro.chatglm.sdk.interceptor;

import com.huiiro.chatglm.sdk.session.Configuration;
import com.huiiro.chatglm.sdk.utils.TokenUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OpenAiHTTPInterceptor implements Interceptor {

    private final Configuration configuration;

    public OpenAiHTTPInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .url(original.url())
                .header("Authorization", "Bearer " + TokenUtils.getToken(configuration.getApiKey(), configuration.getApiSecret()))
                .header("Content-Type", Configuration.JSON_CONTENT_TYPE)
                .header("User-Agent", Configuration.DEFAULT_USER_AGENT)
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
