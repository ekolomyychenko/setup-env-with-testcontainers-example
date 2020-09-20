package setup.steps.example;

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
public class TestContainersHelper {

    public String getImage(@NonNull String service) throws InterruptedException {
        String image = service + ":" + ConfigHelper.getConfig().getString(service + ".version");
        return image;
    }

    public GenericContainer setConfig(@NonNull GenericContainer genericContainer) throws IOException {
        genericContainer = setPorts(genericContainer);
        genericContainer = setJavaOps(genericContainer);
        genericContainer = setResourceMapping(genericContainer);
        genericContainer = setEnvVars(genericContainer);
        genericContainer = setNetworkAlias(genericContainer);
        genericContainer = setWithCommand(genericContainer);
        return genericContainer;
    }

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

    private String getImageName(@NonNull GenericContainer genericContainer){
        return genericContainer.getLabels().get("image-name").toString();
    }

    private GenericContainer setNetworkAlias(@NonNull GenericContainer genericContainer) {
        String networkAliases = getImageName(genericContainer);
        String networkAliasesFromConf = null;
        try {
            networkAliasesFromConf = ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".network-alias");
        } catch (ConfigException.Missing e) { log.info(e); }
        if (networkAliasesFromConf != null) {
            networkAliases = networkAliasesFromConf;
        }
        genericContainer.withNetworkAliases(networkAliases);
        return genericContainer;
    }


    private GenericContainer setPorts(@NonNull GenericContainer genericContainer) {

        ArrayList<Integer> ports = new ArrayList<>();

        try {
            ports.add(ConfigHelper.getConfig().getInt(getImageName(genericContainer) + ".grpc-port"));
        } catch (ConfigException.Missing e) { log.info(e); }

        try {
            ports.add(ConfigHelper.getConfig().getInt(getImageName(genericContainer) + ".http-port"));
        } catch (ConfigException.Missing e) { log.info(e); }

        if (ports.size() != 0) {
            for (int port : ports) {
                log.info("Exposed ports for " + getImageName(genericContainer) + " " + port);
                genericContainer.setExposedPorts(ports);
            }
        }

        return genericContainer;
    }


    private GenericContainer setJavaOps(@NonNull GenericContainer genericContainer) {

        try {
            genericContainer.withEnv("JAVA_OPTS", ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".java-ops"));
        } catch (ConfigException.Missing e) { log.info(e); }

        return genericContainer;
    }

    private GenericContainer setWithCommand(@NonNull GenericContainer genericContainer) {

        try {
            genericContainer.withCommand(ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".command"));
        } catch (ConfigException.Missing e) { log.info(e); }

        return genericContainer;
    }

    private GenericContainer setResourceMapping(@NonNull GenericContainer genericContainer) {

        try {
            genericContainer.withClasspathResourceMapping(
                    ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".resource-mapping-resource-path"),
                    ConfigHelper.getConfig().getString(getImageName(genericContainer) + ".resource-mapping-container-path"),
                    BindMode.READ_WRITE);
        } catch (ConfigException.Missing e) { log.info(e); }

        return genericContainer;
    }

    private GenericContainer setEnvVars(@NonNull GenericContainer genericContainer) throws IOException {

        Map<String, String> env = ConfigHelper.getEnvVars();

        Iterator<Map.Entry<String, String>> iterator = env.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> pair = iterator.next();
            genericContainer.withEnv(pair.getKey(), pair.getValue());
        }

        return genericContainer;
    }
}
