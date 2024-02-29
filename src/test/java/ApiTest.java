import com.alibaba.fastjson.JSON;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionRequest;
import com.huiiro.chatglm.sdk.model.chat.ChatCompletionResponse;
import com.huiiro.chatglm.sdk.model.common.Model;
import com.huiiro.chatglm.sdk.model.common.Role;
import com.huiiro.chatglm.sdk.session.Configuration;
import com.huiiro.chatglm.sdk.session.OpenAiSession;
import com.huiiro.chatglm.sdk.session.OpenAiSessionFactory;
import com.huiiro.chatglm.sdk.session.defaults.DefaultOpenAiSessionFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("1eb8f9468bb576975340559d416b7a03.vsdYHxfxUH4nQl6r");
        configuration.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }

    @Test
    public void test_completions_new() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(Model.GLM_3_5_TURBO);
        request.setIsCompatible(false);
        // 24年1月发布的 glm-3-turbo、glm-4 支持函数、知识库、联网功能
        request.setTools(new ArrayList<ChatCompletionRequest.Tool>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Tool.builder()
                        .type(ChatCompletionRequest.Tool.Type.web_search)
                        .webSearch(ChatCompletionRequest.Tool.WebSearch.builder().enable(true).searchQuery("小傅哥").build())
                        .build());
            }
        });
        request.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(ChatCompletionRequest.Prompt.builder()
                        .role(Role.user.getCode())
                        .content("你好")
                        .build());
            }
        });

        openAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                    return;
                }

                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果：{}", JSON.toJSONString(response));
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("对话完成");
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("对话失败", t);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }
}
