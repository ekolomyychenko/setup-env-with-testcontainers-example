package setup.steps.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SetupEnvTestExample extends BaseTest {

    Service mongo = createGenericContainer("mongo");
    Service consul = createGenericContainer("consul");
    Service authorization = createGenericContainer("reportportal/service-authorization");
    Service traefik = createGenericContainer("traefik");
    Service index = createGenericContainer("reportportal/service-index");
    Service api = createGenericContainer("reportportal/service-api");
    Service ui = createGenericContainer("reportportal/service-ui");
    Service analyzer = createGenericContainer("reportportal/service-analyzer");
    Service elasticsearch = createGenericContainer("docker.elastic.co/elasticsearch/elasticsearch-oss");
    Service jira = createGenericContainer("reportportal/service-jira");
    Service rally = createGenericContainer("reportportal/service-rally");

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
