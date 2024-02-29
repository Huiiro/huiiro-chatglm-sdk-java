package com.huiiro.chatglm.sdk.session.defaults;

import com.huiiro.chatglm.sdk.IOpenAiApi;
import com.huiiro.chatglm.sdk.executor.Executor;
import com.huiiro.chatglm.sdk.interceptor.OpenAiHTTPInterceptor;
import com.huiiro.chatglm.sdk.model.common.Model;
import com.huiiro.chatglm.sdk.session.Configuration;
import com.huiiro.chatglm.sdk.session.OpenAiSession;
import com.huiiro.chatglm.sdk.session.OpenAiSessionFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DefaultOpenAiSessionFactory implements OpenAiSessionFactory {

    private final Configuration configuration;

    public DefaultOpenAiSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OpenAiSession openSession() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(configuration.getLevel());

        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OpenAiHTTPInterceptor(configuration))
                .connectTimeout(configuration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(configuration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(configuration.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        configuration.setOkHttpClient(okHttpClient);

        IOpenAiApi openAiApi = new Retrofit.Builder()
                .baseUrl(configuration.getApiHost())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(IOpenAiApi.class);

        configuration.setOpenAiApi(openAiApi);

        HashMap<Model, Executor> executorGroup = configuration.newExecutorGroup();

        return new DefaultOpenAiSession(configuration, executorGroup);
    }
}
