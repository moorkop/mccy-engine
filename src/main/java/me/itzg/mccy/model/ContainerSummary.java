package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spotify.docker.client.messages.Container;
import me.itzg.mccy.types.MccyConstants;

import java.net.URI;
import java.util.List;

/**
 * Provides a summary of very useful info that's buried in the usual container info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerSummary {
    private String id;

    private String name;

    private String hostIp;

    private int hostPort;

    private Boolean running;

    private String status;

    private URI icon;

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public Boolean isRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

    public URI getIcon() {
        return icon;
    }

    public void setIcon(URI icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static ContainerSummary from(Container container, String dockerHostIp) {
        final ContainerSummary summary = new ContainerSummary();

        summary.setId(container.id());
        summary.setName(normalizeName(container.names().get(0)));
        summary.setStatus(container.status());
        summary.fromPortMapping(container.ports(), dockerHostIp);

        return summary;
    }

    private void fromPortMapping(List<Container.PortMapping> portMapping, String dockerHostIp) {
        portMapping.stream()
                .filter(pm -> pm.getPrivatePort() == MccyConstants.SERVER_CONTAINER_PORT_INT)
                .findFirst()
                .ifPresent(pm -> {
                    setHostPort(pm.getPublicPort());
                    final String ip = pm.getIp();
                    if (ip.equals("0.0.0.0")) {
                        setHostIp(dockerHostIp);
                    }
                    else {
                        setHostIp(ip);
                    }
                });
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private static String normalizeName(String rawName) {
        final int i = rawName.lastIndexOf("/");
        if (i >= 0) {
            return rawName.substring(i+1);
        }
        return rawName;
    }
}
