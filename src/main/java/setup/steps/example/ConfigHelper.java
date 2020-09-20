package setup.steps.example;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Log4j2
public class ConfigHelper {

    private static final Config config = ConfigFactory.load();

    public static Config getConfig() {
        return config;
    }

    /*
     * Переменные окружения для микросервисов
     * */
    public static Map<String, String> getEnvVars() throws IOException {
        Map<String, String> envVars = new HashMap<>();
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/services-env.properties"));

        for (String key : properties.stringPropertyNames()) {
            envVars.put(key, properties.get(key).toString());
        }

        return envVars;
    }
}
