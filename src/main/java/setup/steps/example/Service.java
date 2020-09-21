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
public class Service extends GenericContainer<Service> {

    private static final Network network = Network.newNetwork();
    private String serviceName;

    @SneakyThrows
    public Service(@NonNull String serviceName) {
        super(getImage(serviceName));
        this.serviceName = serviceName.replaceAll("\"", "");
    }

    @SneakyThrows
    @Override
    protected void configure() {
        withLabel("image-name", serviceName);
        withNetwork(network);
        setPorts();
        setJavaOps();
        setResourceMapping();
        setEnvVars();
        setNetworkAlias();
        setWithCommand();
    }

    public static String getImage(@NonNull String service) {
        log.info("Service name: " + service);
        return service + ":" + ConfigHelper.getConfig().getString(service + ".version");
    }

    public static void logging(@NonNull Service genericContainer) {
        genericContainer.followOutput(new Consumer<OutputFrame>() {
            @Override
            public void accept(OutputFrame outputFrame) {
                System.out.println(String.format(
                        "%s: %s",
                        genericContainer
                                .getDockerImageName(),
                        outputFrame.getUtf8String()
                ));
            }
        });
    }

    public static Boolean shouldEnableLogging(Service genericContainer) {
        log.info(genericContainer.getLabels());
        return ConfigHelper.getConfig().getBoolean(genericContainer.getLabels().get("image-name") + ".logging-enabled");
    }

    private void setNetworkAlias() {
        String networkAliases = serviceName;
        String networkAliasesFromConf = null;
        try {
            networkAliasesFromConf = ConfigHelper.getConfig().getString(serviceName + ".network-alias");
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
        if (networkAliasesFromConf != null) {
            networkAliases = networkAliasesFromConf;
        }
        withNetworkAliases(networkAliases);
    }


    private void setPorts() {
        ArrayList<Integer> ports = new ArrayList<>();
        try {
            ports.add(ConfigHelper.getConfig().getInt(serviceName + ".grpc-port"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
        try {
            ports.add(ConfigHelper.getConfig().getInt(serviceName + ".http-port"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
        if (ports.size() != 0) {
            for (int port : ports) {
                log.info("Exposed ports for " + serviceName + " " + port);
                setExposedPorts(ports);
            }
        }
    }


    private void setJavaOps() {
        try {
            withEnv("JAVA_OPTS", ConfigHelper.getConfig().getString(serviceName + ".java-ops"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
    }

    private void setWithCommand() {
        try {
            withCommand(ConfigHelper.getConfig().getString(serviceName + ".command"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
    }

    private void setResourceMapping() {
        try {
            withClasspathResourceMapping(
                    ConfigHelper.getConfig().getString(serviceName + ".resource-mapping-resource-path"),
                    ConfigHelper.getConfig().getString(serviceName + ".resource-mapping-container-path"),
                    BindMode.READ_WRITE);
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
    }

    private void setEnvVars() throws IOException {
        Map<String, String> env = ConfigHelper.getEnvVars();
        for (Map.Entry<String, String> pair : env.entrySet()) {
            withEnv(pair.getKey(), pair.getValue());
        }
    }

}
