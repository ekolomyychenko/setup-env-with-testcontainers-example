package setup.steps.example;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


public class SetupAllEnvTestExample extends SetupAllEnvBaseTest {

    @Test
    public void setup_env_test() throws InterruptedException {
        given()
                .baseUri("http://localhost:" + services.get("reportportal/service-ui").getMappedPort(8080))
                .when()
                .post()
                .then()
                .statusCode(200);
    }
}
