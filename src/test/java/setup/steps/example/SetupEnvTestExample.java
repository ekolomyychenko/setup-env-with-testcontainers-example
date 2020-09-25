package setup.steps.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class SetupEnvTestExample extends BaseTest {

    ServiceContainer mongo = createServiceContainer("mongo");
    ServiceContainer consul = createServiceContainer("consul");
    ServiceContainer authorization = createServiceContainer("reportportal/service-authorization");
    ServiceContainer traefik = createServiceContainer("traefik");
    ServiceContainer index = createServiceContainer("reportportal/service-index");
    ServiceContainer api = createServiceContainer("reportportal/service-api");
    ServiceContainer ui = createServiceContainer("reportportal/service-ui");
    ServiceContainer analyzer = createServiceContainer("reportportal/service-analyzer");
    ServiceContainer elasticsearch = createServiceContainer("docker.elastic.co/elasticsearch/elasticsearch-oss");
    ServiceContainer jira = createServiceContainer("reportportal/service-jira");
    ServiceContainer rally = createServiceContainer("reportportal/service-rally");

    public SetupEnvTestExample() {
    }

    @BeforeEach
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
        given()
                .baseUri("http://localhost:" + ui.getMappedPort(8080))
                .when()
                .post()
                .then()
                .statusCode(200);
    }
}
