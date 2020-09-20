package test.steps.example;

import org.junit.After;
import org.junit.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

public class SetupEnvTestExample extends BaseTest {


    @Test
    public void setup_env_test() throws IOException, InterruptedException {
        GenericContainer mongo = createGenericContainer("mongo");
        GenericContainer consul = createGenericContainer("consul");
        GenericContainer authorization = createGenericContainer("reportportal/service-authorization");
        GenericContainer traefik = createGenericContainer("traefik");
        GenericContainer index = createGenericContainer("reportportal/service-index");
        GenericContainer api = createGenericContainer("reportportal/service-api");
        GenericContainer ui = createGenericContainer("reportportal/service-ui");
        GenericContainer analyzer = createGenericContainer("reportportal/service-analyzer");
        GenericContainer elasticsearch = createGenericContainer("docker.elastic.co/elasticsearch/elasticsearch-oss");
        GenericContainer jira = createGenericContainer("reportportal/service-jira");
        GenericContainer rally = createGenericContainer("reportportal/service-rally");


        setup(mongo);
        setup(consul);
        setup(authorization);
        setup(traefik);
        setup(index);
        setup(api);
        setup(ui);
        setup(analyzer);
        setup(elasticsearch);
        setup(jira);
        setup(rally);


        Thread.sleep(15_000);

        System.out.println(ui.getMappedPort(8080));
    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(60_000);
    }


}
