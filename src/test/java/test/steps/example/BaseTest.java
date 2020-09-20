package test.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.List;

@Log4j2
public class BaseTest {

    private TestContainersHelpers testContainersHelpers = new TestContainersHelpers();


    protected GenericContainer createGenericContainer(@NonNull String service) throws InterruptedException, IOException {

        String image = testContainersHelpers.getImage(service);

        GenericContainer genericContainer = new GenericContainer(image)
                .withNetwork(Session.getNetwork())
                .withNetworkAliases(service);

        genericContainer = testContainersHelpers.setPorts(genericContainer, service);
        genericContainer = testContainersHelpers.setJavaOps(genericContainer, service);
        genericContainer = testContainersHelpers.setResourceMapping(genericContainer, service);
        genericContainer = testContainersHelpers.setEnvVars(genericContainer);
        genericContainer = testContainersHelpers.setNetworkAlias(genericContainer, service);
        genericContainer = testContainersHelpers.setWithCommand(genericContainer, service);

        Session.addContainer(genericContainer);

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
        return ConfigHelper.getConfig().getBoolean(genericContainer.getNetworkAliases().get(1) + ".logging-enabled");
    }
}
