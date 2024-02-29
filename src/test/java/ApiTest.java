import com.huiiro.chatglm.sdk.session.Configuration;
import com.huiiro.chatglm.sdk.session.OpenAiSession;
import com.huiiro.chatglm.sdk.session.OpenAiSessionFactory;
import com.huiiro.chatglm.sdk.session.defaults.DefaultOpenAiSessionFactory;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;

public class ApiTest {

    private OpenAiSession openAiSession;

    @Before
    public void test_OpenAiSessionFactory() {
        // 1. 配置文件
        Configuration configuration = new Configuration();
        configuration.setApiHost("https://open.bigmodel.cn/");
        configuration.setApiSecretKey("62ddec38b1d0b9a7b0fddaf271e6ed90.HpD0SUBUlvqd05ey");
        configuration.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 2. 会话工厂
        OpenAiSessionFactory factory = new DefaultOpenAiSessionFactory(configuration);
        // 3. 开启会话
        this.openAiSession = factory.openSession();
    }
}
