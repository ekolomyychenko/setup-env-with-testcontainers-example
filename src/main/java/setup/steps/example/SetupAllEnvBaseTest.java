package setup.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;

@Log4j2
public class SetupAllEnvBaseTest {

    @BeforeAll
    public void beforeAll(){
        // достать все сервисы из апликейшена
        // положить например в мапу Map<String, Object> <название сервиса, null>
        // пройтись по мапе и для кадого значения вызвать createServiceContainer, контейнеры можно тоже сохранить в мапе <название сервиса, ServiceContainer>
        // протись по мапе достать велью и кинуть в setup(@NonNull ServiceContainer container)
        // если че-то не стартанулось, то где-то через 30 сек все упадет, docker ps -a
    }

    protected ServiceContainer createServiceContainer(@NonNull String service) {
        return new ServiceContainer(service);
    }

    protected void setup(@NonNull ServiceContainer container) {
        container.start();

        log.info("Setup: " + container.getDockerImageName());
        List<Integer> ports = container.getExposedPorts();
        for (int port : ports) {
            log.info("Setup: " + container.getDockerImageName() + " with port: " + port + " -> " + container.getMappedPort(port));
        }
        if (Boolean.parseBoolean(container.getLabels().get("logging-enabled"))) {
            ServiceContainer.logging(container);
        }
    }

}
