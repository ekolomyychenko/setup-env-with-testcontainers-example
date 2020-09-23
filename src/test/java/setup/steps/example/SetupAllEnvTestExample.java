package setup.steps.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SetupAllEnvTestExample extends BaseTest {


    @Test
    public void setup_env_test() throws InterruptedException {
        Thread.sleep(120_000);

    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(60_000);
    }


}
