package test.steps.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public class SetupEnvTestExample extends BaseTest {

    GenericContainer mongo = createGenericContainer("mongo");
    GenericContainer consul = createGenericContainer("consul");
    GenericContainer authorization = createGenericContainer("reportportal/service-authorization");
    GenericContainer traefik = createGenericContainer("traefik");
    GenericContainer index = createGenericContainer("reportportal/service-index");
    GenericContainer api = createGenericContainer("reportportal/service-api");
    GenericContainer ui = createGenericContainer("reportportal/service-ui");
    GenericContainer analyzer = createGenericContainer("reportportal/service-analyzer");
    GenericContainer elasticsearch = createGenericContainer("docker.elastic.co/elasticsearch/elasticsearch-oss");
    GenericContainer jira = createGenericContainer("reportportal/service-jira");
    GenericContainer rally = createGenericContainer("reportportal/service-rally");

    public SetupEnvTestExample() throws IOException, InterruptedException {
    }

    @Before
    public void before() {
        setup(mongo);
        setup(consul);
        setup(authorization);
        setup(traefik);
        setup(index);
        setup(api);
        setup(analyzer);
        setup(elasticsearch);
        setup(jira);
        setup(rally);
        setup(ui);
    }

    @Test
    public void setup_env_test() {
        System.out.println(ui.getMappedPort(8080));
    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(60_000);
    }


}
