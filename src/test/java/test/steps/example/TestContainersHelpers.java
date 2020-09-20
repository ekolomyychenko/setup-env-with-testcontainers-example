package test.steps.example;

import com.typesafe.config.ConfigException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
public class TestContainersHelpers {

    public void logging(@NonNull GenericContainer genericContainer) {
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


    public String getImage(@NonNull String service) throws InterruptedException {
        String image = service + ":" + ConfigHelper.getConfig().getString(service + ".version");
        return image;
    }

    public GenericContainer setNetworkAlias(@NonNull GenericContainer genericContainer, @NonNull String service) {
        String networkAliases = service;
        String networkAliasesFromConf = null;
        try {
            networkAliasesFromConf = ConfigHelper.getConfig().getString(service + ".network-alias");
        } catch (ConfigException.Missing e) {
            log.info(e);
        }
        if (networkAliasesFromConf != null) {
            networkAliases = networkAliasesFromConf;
        }
        genericContainer.withNetworkAliases(networkAliases);
        return genericContainer;
    }


    public GenericContainer setPorts(@NonNull GenericContainer genericContainer, @NonNull String service) {

        ArrayList<Integer> ports = new ArrayList<>();
//        ports.add(5005);

        try {
            ports.add(ConfigHelper.getConfig().getInt(service + ".grpc-port"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        try {
            ports.add(ConfigHelper.getConfig().getInt(service + ".http-port"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        if (ports.size() != 0) {
            for (int port : ports) {
                log.info("Exposed ports for " + service + " " + port);
                genericContainer.setExposedPorts(ports);
            }
        }

        return genericContainer;
    }


    public GenericContainer setJavaOps(@NonNull GenericContainer genericContainer, @NonNull String service) {

        try {
            genericContainer.withEnv("JAVA_OPTS", ConfigHelper.getConfig().getString(service + ".java-ops"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        return genericContainer;
    }

    public GenericContainer setWithCommand(@NonNull GenericContainer genericContainer, @NonNull String service) {

        try {
            genericContainer.withCommand(ConfigHelper.getConfig().getString(service + ".command"));
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        return genericContainer;
    }

    public GenericContainer setResourceMapping(@NonNull GenericContainer genericContainer, @NonNull String service) {

        try {
            genericContainer.withClasspathResourceMapping(
                    ConfigHelper.getConfig().getString(service + ".resource-mapping-resource-path"),
                    ConfigHelper.getConfig().getString(service + ".resource-mapping-container-path"),
                    BindMode.READ_WRITE);
        } catch (ConfigException.Missing e) {
            log.info(e);
        }

        return genericContainer;
    }

    public GenericContainer setEnvVars(@NonNull GenericContainer genericContainer) throws IOException {

        Map<String, String> env = ConfigHelper.getEnvVars();

        Iterator<Map.Entry<String, String>> iterator = env.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> pair = iterator.next();
            genericContainer.withEnv(pair.getKey(), pair.getValue());
        }

        return genericContainer;
    }
}
