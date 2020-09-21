package setup.steps.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SetupEnvTestExample extends BaseTest {

    ServiceContainer mongo = createGenericContainer("mongo");
    ServiceContainer consul = createGenericContainer("consul");
    ServiceContainer authorization = createGenericContainer("reportportal/service-authorization");
    ServiceContainer traefik = createGenericContainer("traefik");
    ServiceContainer index = createGenericContainer("reportportal/service-index");
    ServiceContainer api = createGenericContainer("reportportal/service-api");
    ServiceContainer ui = createGenericContainer("reportportal/service-ui");
    ServiceContainer analyzer = createGenericContainer("reportportal/service-analyzer");
    ServiceContainer elasticsearch = createGenericContainer("docker.elastic.co/elasticsearch/elasticsearch-oss");
    ServiceContainer jira = createGenericContainer("reportportal/service-jira");
    ServiceContainer rally = createGenericContainer("reportportal/service-rally");

    public SetupEnvTestExample() {
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
    public void setup_env_test() throws InterruptedException {
        System.out.println(ui.getMappedPort(8080));
        Thread.sleep(60_000);

    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(60_000);
    }


}
