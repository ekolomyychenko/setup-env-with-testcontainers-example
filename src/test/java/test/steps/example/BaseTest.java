package test.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.io.IOException;
import java.util.List;

@Log4j2
public class BaseTest {

    private TestContainersHelper testContainersHelpers = new TestContainersHelper();

    private static Network network = Network.newNetwork();

    protected GenericContainer createGenericContainer(@NonNull String service) throws InterruptedException, IOException {

        String image = testContainersHelpers.getImage(service);

        GenericContainer genericContainer = new GenericContainer(image)
                .withLabel("image-name", service)
                .withNetwork(network);

        genericContainer = testContainersHelpers.setConfig(genericContainer);

        return genericContainer;
    }

    protected void setup(@NonNull GenericContainer genericContainer) {
        genericContainer.start();

        log.info("Setup: " + genericContainer.getDockerImageName());
        List<Integer> ports = genericContainer.getExposedPorts();
        for (int port : ports) {
            log.info("Setup: " + genericContainer.getDockerImageName() + " with port: " + port + " -> " + genericContainer.getMappedPort(port));
        }
        if (shouldEnableLogging(genericContainer)) {
            testContainersHelpers.logging(genericContainer);
        }
    }

    private Boolean shouldEnableLogging(@NonNull GenericContainer genericContainer){
        log.info(genericContainer.getLabels());
        return ConfigHelper.getConfig().getBoolean(genericContainer.getLabels().get("image-name") + ".logging-enabled");
    }
}
