package setup.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static setup.steps.example.ConfigHelper.getConfig;

@Log4j2
public class SetupAllEnvBaseTest {

    public static final Map<String, ServiceContainer> services = new HashMap<>();

    @BeforeAll
    static void beforeAll() {
        for (String service : getConfig().getStringList("services")) {
            services.put(service, new ServiceContainer(service));
        }
        for (Map.Entry<String, ServiceContainer> entry : services.entrySet()) {
            setup(entry.getValue());
        }
    }

    private static void setup(@NonNull ServiceContainer container) {
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
