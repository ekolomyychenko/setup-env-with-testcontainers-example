package ui.test.steps.example;

import org.junit.After;
import org.junit.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.MountableFile;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

public class UiTestExample {


    @Test
    public void test() throws InterruptedException {
        Network network = Network.newNetwork();


        GenericContainer mongodb = new GenericContainer("mongo:3.4")
                .withNetwork(network)
                .withClasspathResourceMapping("/",
                        "/data/db",
                        BindMode.READ_WRITE)
                .withNetworkAliases("mongodb");

        GenericContainer registry = new GenericContainer("consul:1.0.6")
                .withNetwork(network)
                .withClasspathResourceMapping("/",
                        "/consul/data",
                        BindMode.READ_WRITE)
                .withCommand("agent -server -bootstrap-expect=1 -ui -client 0.0.0.0")
                .withEnv("CONSUL_LOCAL_CONFIG", "{\"leave_on_terminate\": true}")
                .withNetworkAliases("registry");

        GenericContainer uat = new GenericContainer("reportportal/service-authorization:4.2.0")
                .withNetwork(network)
                .withEnv("RP_PROFILES", "docker")
                .withEnv("RP_SESSION_LIVE", "86400")
                .withNetworkAliases("uat");

        GenericContainer gateway = new GenericContainer("traefik:1.6.6")
                .withNetwork(network)
//                .withExposedPorts(8080)
                .withCommand(
                        "--consulcatalog.endpoint=registry:8500",
                        "--defaultEntryPoints=http",
                        "--entryPoints=Name:http Address::8080",
                        "--web",
                        "--web.address=:8081")
                .withNetworkAliases("gateway");

        GenericContainer index = new GenericContainer("reportportal/service-index:4.2.0")
                .withNetwork(network)
                .withEnv("RP_SERVER_PORT", "8080")
                .withEnv("RP_PROXY_CONSUL", "true")
                .withNetworkAliases("index");


        GenericContainer api = new GenericContainer("reportportal/service-api:4.3.0")
                .withNetwork(network)
                .withEnv("RP_PROFILES", "docker")
                .withEnv("JAVA_OPTS", "-Xmx1g -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp")
                .withNetworkAliases("api");


        GenericContainer ui = new GenericContainer("reportportal/service-ui:4.3.0")
                .withNetwork(network)
                .withEnv("RP_SERVER.PORT", "8080")
                .withEnv("RP_CONSUL.TAGS", "urlprefix-/ui opts strip=/ui")
                .withEnv("RP_CONSUL.ADDRESS", "registry:8500")
                .withNetworkAliases("ui");


        GenericContainer analyzer = new GenericContainer("reportportal/service-analyzer:4.3.0")
                .withNetwork(network)
                .withNetworkAliases("analyzer");


        GenericContainer elasticsearch = new GenericContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.1.1")
                .withNetwork(network)
                .withClasspathResourceMapping("/",
                        "/usr/share/elasticsearch/data",
                        BindMode.READ_WRITE)
                .withEnv("bootstrap.memory_lock", "true")
                .withNetworkAliases("elasticsearch");


        GenericContainer jira = new GenericContainer("reportportal/service-jira:4.3.0")
                .withNetwork(network)
                .withEnv("RP_PROFILES", "docker")
                .withNetworkAliases("jira");


        GenericContainer rally = new GenericContainer("reportportal/service-rally:4.3.0")
                .withNetwork(network)
                .withEnv("RP_PROFILES", "docker")
                .withNetworkAliases("rally");


        mongodb.start();
        registry.start();
        uat.start();
        gateway.start();
        index.start();
        api.start();
        ui.start();
        analyzer.start();
        elasticsearch.start();
        jira.start();
        rally.start();

        Thread.sleep(15_000);

        System.out.println(ui.getMappedPort(8080));


        Thread.sleep(180_000);


    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(20_000);
    }


}
