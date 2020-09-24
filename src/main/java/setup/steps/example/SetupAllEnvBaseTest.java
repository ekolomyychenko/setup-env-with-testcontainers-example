package setup.steps.example;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class SetupAllEnvBaseTest {

    @BeforeAll
    public static void beforeAll(){

        Map<String,Object> metadata = new HashMap<>();

        metadata.put("mongo", ConfigHelper.getConfig().getObject("services.mongo"));
        metadata.put("consul", ConfigHelper.getConfig().getObject("services.consul"));
        metadata.put("reportportal/service-authorization", ConfigHelper.getConfig().getObject("services.reportportal/service-authorization"));
        metadata.put("traefik", ConfigHelper.getConfig().getObject("services.traefik"));
        metadata.put("reportportal/service-index", ConfigHelper.getConfig().getObject("services.reportportal/service-index"));
        metadata.put("reportportal/service-api", ConfigHelper.getConfig().getObject("services.reportportal/service-api"));
        metadata.put("reportportal/service-ui", ConfigHelper.getConfig().getObject("services.reportportal/service-ui"));
        metadata.put("reportportal/service-analyzer", ConfigHelper.getConfig().getObject("services.reportportal/service-analyzer"));
        metadata.put("docker.elastic.co/elasticsearch/elasticsearch-oss", ConfigHelper.getConfig().getObject("services.docker.elastic.co/elasticsearch/elasticsearch-oss"));
        metadata.put("reportportal/service-jira", ConfigHelper.getConfig().getObject("services.reportportal/service-jira"));
        metadata.put("reportportal/service-rally", ConfigHelper.getConfig().getObject("services.reportportal/service-rally"));

        Map<String, ServiceContainer> containersMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : metadata.entrySet())
        {
            ServiceContainer serviceContainer = createServiceContainer (entry.getKey());
            containersMap.put(entry.getKey(),serviceContainer);
        }

        for (Map.Entry<String, ServiceContainer> entry : containersMap.entrySet())
        {
            setup(entry.getValue());
        }


        // достать все сервисы из апликейшена
        // положить например в мапу Map<String, Object> <название сервиса, null>
        // пройтись по мапе и для кадого значения вызвать createServiceContainer, контейнеры можно тоже сохранить в мапе <название сервиса, ServiceContainer>
        // протись по мапе достать велью и кинуть в setup(@NonNull ServiceContainer container)
        // если че-то не стартанулось, то где-то через 30 сек все упадет, docker ps -a
    }

    protected static ServiceContainer createServiceContainer(@NonNull String service) {
        return new ServiceContainer(service);
    }

    protected static void setup(@NonNull ServiceContainer container) {
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
