package setup.steps.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


public class SetupAllEnvTestExample extends BaseTest {

    @Test
    public void setup_env_test() throws InterruptedException {
        Thread.sleep(120_000);

    }

    @AfterEach
    public void after() throws InterruptedException {
        Thread.sleep(60_000);
    }
}
