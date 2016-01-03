package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.PortBinding;
import me.itzg.mccy.types.MccyConstants;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Provides a summary of very useful info that's buried in the usual container info
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerSummary {
    private String id;

    private String name;

    private String type;

    private String version;

    private String hostIp;

    private int hostPort;

    private Boolean running;

    private String status;

    private URI icon;

    private URI modpack;

    private Map<String,String> labels;

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

    public static ContainerSummary from(ContainerInfo info, String dockerHostIp) {
        ContainerSummary summary = new ContainerSummary();

        final Map<String, List<PortBinding>> ports = info.networkSettings().ports();
        // will be null when the container is stopped
        if (ports != null) {
            final List<PortBinding> portBindings = ports.get(MccyConstants.SERVER_CONTAINER_PORT);
            final PortBinding portBinding = portBindings.get(0);

            final String reportedHostIp = portBinding.hostIp();
            final String resolvedIp;
            if (reportedHostIp.equals(MccyConstants.IP_ADDR_ALL_IF)) {
                resolvedIp = dockerHostIp;
            }
            else {
                resolvedIp = reportedHostIp;
            }
            try {
                summary.setHostIp(InetAddress.getByName(resolvedIp).getCanonicalHostName());
            } catch (UnknownHostException e) {
                summary.setHostIp(resolvedIp);
            }
            summary.setHostPort(Integer.parseInt(portBinding.hostPort()));
        }

        summary.setRunning(info.state().running());

        final List<String> env = info.config().env();
        fillFromEnv(env, MccyConstants.ENV_ICON, v -> {
            summary.setIcon(URI.create(v));
        });
        fillFromEnv(env, MccyConstants.ENV_TYPE, v -> {
            summary.setType(v);
        });
        fillFromEnv(env, MccyConstants.ENV_VERSION, v -> {
            summary.setVersion(v);
        });

        summary.setName(normalizeName(info.name()));

        summary.setId(info.id());

        summary.setLabels(info.config().labels().entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(MccyConstants.MCCY_LABEL_PREFIX))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        final String value = summary.getLabels().get(MccyConstants.MCCY_LABEL_MODPACK_URL);
        if (value != null) {
            summary.setModpack(URI.create(value));
        }

        return summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private static void fillFromEnv(List<String> env, String envKey, Consumer<String> consumer) {
        final String prefix = envKey + "=";

        for (String e : env) {
            if (e.startsWith(prefix)) {
                consumer.accept(e.substring(prefix.length()));
            }
        }
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public URI getModpack() {
        return modpack;
    }

    public void setModpack(URI modpack) {
        this.modpack = modpack;
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
