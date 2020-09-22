package setup.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class BaseTest {

    protected ServiceContainer createGenericContainer(@NonNull String service) {
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
