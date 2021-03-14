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
		getConfig().getStringList("services").forEach(service ->
				services.put(service, new ServiceContainer(service)));
		services.values().forEach(SetupAllEnvBaseTest::setup);
	}

	private static void setup(@NonNull ServiceContainer container) {
		container.start();

		log.info("Setup: " + container.getDockerImageName());
		container.getExposedPorts().forEach(port -> log.info(String.format("Setup: %s with port: %s -> %s",
				container.getDockerImageName(), port, container.getMappedPort(port))));
		if (Boolean.parseBoolean(container.getLabels().get("logging-enabled"))) {
			ServiceContainer.logging(container);
		}
	}

}
