package setup.steps.example;

import com.typesafe.config.ConfigException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
public class ContainerHelper {

    public static String getImage(@NonNull String service) throws InterruptedException {
        return service + ":" + ConfigHelper.getConfig().getString(service + ".version");
    }

    public static GenericContainer setConfig(@NonNull GenericContainer genericContainer) throws IOException {
        setPorts(genericContainer);
        setJavaOps(genericContainer);
        setResourceMapping(genericContainer);
        setEnvVars(genericContainer);
        setNetworkAlias(genericContainer);
        setWithCommand(genericContainer);
        return genericContainer;
    }

    public static void logging(@NonNull GenericContainer genericContainer) {
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

    public static Boolean shouldEnableLogging(@NonNull GenericContainer genericContainer) {
        log.info(genericContainer.getLabels());
        return ConfigHelper.getConfig().getBoolean(genericContainer.getLabels().get("image-name") + ".logging-enabled");
    }

    private static String getImageName(@NonNull GenericContainer genericContainer) {
        return genericContainer.getLabels().get("image-name").toString();
    }

    private static void setNetworkAlias(@NonNull GenericContainer genericContainer) {
        String networkAliases = getImageName(genericContainer);
        String networkAliasesFromConf = null;
        try {
            networkAliasesFromConf = ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".network-alias");
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
        if (networkAliasesFromConf != null) {
            networkAliases = networkAliasesFromConf;
        }
        genericContainer.withNetworkAliases(networkAliases);
    }


    private static void setPorts(@NonNull GenericContainer genericContainer) {
        ArrayList<Integer> ports = new ArrayList<>();

        try {
            ports.add(ConfigHelper.getConfig().getInt(getImageName(genericContainer) + ".grpc-port"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        try {
            ports.add(ConfigHelper.getConfig().getInt(getImageName(genericContainer) + ".http-port"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        if (ports.size() != 0) {
            for (int port : ports) {
                log.info("Exposed ports for " + getImageName(genericContainer) + " " + port);
                genericContainer.setExposedPorts(ports);
            }
        }
    }


    private static void setJavaOps(@NonNull GenericContainer genericContainer) {
        try {
            genericContainer.withEnv("JAVA_OPTS", ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".java-ops"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
    }

    private static void setWithCommand(@NonNull GenericContainer genericContainer) {
        try {
            genericContainer.withCommand(ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".command"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
    }

    private static void setResourceMapping(@NonNull GenericContainer genericContainer) {
        try {
            genericContainer.withClasspathResourceMapping(
                    ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".resource-mapping-resource-path"),
                    ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".resource-mapping-container-path"),
                    BindMode.READ_WRITE);
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
    }

    private static void setEnvVars(@NonNull GenericContainer genericContainer) throws IOException {
        Map<String, String> env = ConfigHelper.getEnvVars();
        for (Map.Entry<String, String> pair : env.entrySet()) {
            genericContainer.withEnv(pair.getKey(), pair.getValue());
        }
    }

}
