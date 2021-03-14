package setup.steps.example;

import com.typesafe.config.ConfigException;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
public class ServiceContainer extends GenericContainer<ServiceContainer> {

	private static final Network network = Network.newNetwork();
	private String serviceName;

	@SneakyThrows
	public ServiceContainer(@NonNull String serviceName) {
		super(getImage(serviceName));
		this.serviceName = serviceName;
	}

	@SneakyThrows
	@Override
	protected void configure() {
		withLabel("image-name", serviceName);
		withLabel("logging-enabled", shouldEnableLogging(serviceName).toString());
		withNetwork(network);
		setupPorts();
		setupJavaOps();
		setupResourceMapping();
		setupEnvVars();
		setupNetworkAlias();
		setupWithCommand();
	}

	public static String getImage(@NonNull String service) {
		log.info("Service name: " + service);
		return service + ":" + ConfigHelper.getConfig().getString(service + ".version");
	}

	public static void logging(@NonNull ServiceContainer container) {
		container.followOutput(new Consumer<OutputFrame>() {
			@Override
			public void accept(OutputFrame outputFrame) {
				System.out.println(String.format(
						"%s: %s",
						container.getDockerImageName(),
						outputFrame.getUtf8String()
				));
			}
		});
	}

	public Boolean shouldEnableLogging(String serviceName) {
		return ConfigHelper.getConfig().getBoolean(serviceName + ".logging-enabled");
	}

	private void setupNetworkAlias() {
		String networkAliases = serviceName;
		String networkAliasesFromConf = null;
		try {
			networkAliasesFromConf = ConfigHelper.getConfig().getString(serviceName + ".network-alias");
		} catch (ConfigException.Missing e) {
			log.warn(e);
		}
		if (networkAliasesFromConf != null) {
			networkAliases = networkAliasesFromConf;
		}
		withNetworkAliases(networkAliases);
	}


	private void setupPorts() {
		ArrayList<Integer> ports = new ArrayList<>();
		try {
			ports.add(ConfigHelper.getConfig().getInt(serviceName + ".grpc-port"));
		} catch (ConfigException.Missing e) {
			log.warn(e);
		}
		try {
			ports.add(ConfigHelper.getConfig().getInt(serviceName + ".http-port"));
		} catch (ConfigException.Missing e) {
			log.warn(e);
		}
		if (ports.size() != 0) {
			ports.forEach(port -> {
				setExposedPorts(ports);
				log.info(String.format("Exposed ports for %s %s", serviceName, port));
			});
		}
	}


	private void setupJavaOps() {
		try {
			withEnv("JAVA_OPTS", ConfigHelper.getConfig().getString(serviceName + ".java-ops"));
		} catch (ConfigException.Missing e) {
			log.warn(e);
		}
	}

	private void setupWithCommand() {
		try {
			withCommand(ConfigHelper.getConfig().getString(serviceName + ".command"));
		} catch (ConfigException.Missing e) {
			log.warn(e);
		}
	}

	private void setupResourceMapping() {
		try {
			withClasspathResourceMapping(
					ConfigHelper.getConfig().getString(serviceName + ".resource-mapping-resource-path"),
					ConfigHelper.getConfig().getString(serviceName + ".resource-mapping-container-path"),
					BindMode.READ_WRITE);
		} catch (ConfigException.Missing e) {
			log.warn(e);
		}
	}

	private void setupEnvVars() throws IOException {
		ConfigHelper.getEnvVars().forEach(this::withEnv);
	}
}
