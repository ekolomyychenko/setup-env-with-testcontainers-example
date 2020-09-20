package test.steps.example;

import lombok.extern.log4j.Log4j2;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Session {

    private static List<GenericContainer> containers = new ArrayList<>();

    public static List<GenericContainer> getContainers() {
        return containers;
    }

    public static void addContainer(GenericContainer container) {
        containers.add(container);
    }

    private static Network network = Network.newNetwork();

    public static Network getNetwork() {
        return network;
    }

}
