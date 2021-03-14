package setup.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class BaseTest {

	protected ServiceContainer createServiceContainer(@NonNull String service) {
		return new ServiceContainer(service);
	}

	protected void setup(@NonNull ServiceContainer container) {
		container.start();
		container.getExposedPorts().forEach(port -> log.info(String.format("Setup: %s with port: %s -> %s",
						container.getDockerImageName(), port, container.getMappedPort(port))));
		if (Boolean.parseBoolean(container.getLabels().get("logging-enabled"))) {
			ServiceContainer.logging(container);
		}
	}

}
