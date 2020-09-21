package setup.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class BaseTest {

    protected ServiceContainer createGenericContainer(@NonNull String service) {
        return new ServiceContainer(service);
    }

    protected void setup(@NonNull ServiceContainer genericContainer) {
        genericContainer.start();

        log.info("Setup: " + genericContainer.getDockerImageName());
        List<Integer> ports = genericContainer.getExposedPorts();
        for (int port : ports) {
            log.info("Setup: " + genericContainer.getDockerImageName() + " with port: " + port + " -> " + genericContainer.getMappedPort(port));
        }
        if (ServiceContainer.shouldEnableLogging(genericContainer)) {
            ServiceContainer.logging(genericContainer);
        }
    }

}
