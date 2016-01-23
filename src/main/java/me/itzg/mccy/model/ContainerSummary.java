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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    private Map<String,String> labels;

    @EnvironmentVariable(MccyConstants.ENV_TYPE)
    private ServerType type;

    @EnvironmentVariable(MccyConstants.ENV_VERSION)
    private String version;

    @EnvironmentVariable(MccyConstants.ENV_FORGEVERSION)
    private String forgeVersion;

    @EnvironmentVariable(MccyConstants.ENV_ICON)
    private URI icon;

    @EnvironmentVariable(MccyConstants.ENV_WORLD)
    private URI world;

    @EnvironmentVariable(MccyConstants.ENV_MODPACK)
    private URI modpack;

    @EnvironmentVariable(MccyConstants.ENV_OPS)
    private String[] ops;

    @EnvironmentVariable(MccyConstants.ENV_WHITELIST)
    private String[] whitelist;

    @EnvironmentVariable(MccyConstants.ENV_SEED)
    private String seed;

    @EnvironmentVariable(MccyConstants.ENV_MODE)
    private String mode;

    @EnvironmentVariable(MccyConstants.ENV_MOTD)
    private String motd;

    @EnvironmentVariable(MccyConstants.ENV_PVP)
    private Boolean pvp;

    @EnvironmentVariable(MccyConstants.ENV_LEVEL_TYPE)
    private LevelType levelType;

    @EnvironmentVariable(MccyConstants.ENV_GENERATOR_SETTINGS)
    private String generatorSettings;

    @EnvironmentVariable(MccyConstants.ENV_LEVEL)
    private String level;

    @EnvironmentVariable(MccyConstants.ENV_DIFFICULTY)
    private String difficulty;

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

    public static ContainerSummary from(Container container, String dockerHostIp, Optional<String> connectUsingHost) {
        final ContainerSummary summary = new ContainerSummary();

        summary.setId(container.id());
        summary.setStatus(container.status());
        container.ports().stream()
                .filter(pm -> pm.getPrivatePort() == MccyConstants.SERVER_CONTAINER_PORT_INT)
                .findFirst()
                .ifPresent(pm -> {
                    summary.setHostPort(pm.getPublicPort());
                    final String ip = pm.getIp();
                    if (connectUsingHost.isPresent()) {
                        summary.setHostIp(connectUsingHost.get());
                    }
                    else if (ip.equals(MccyConstants.IP_ADDR_ALL_IF)) {
                        summary.setHostIp(dockerHostIp);
                    }
                    else {
                        summary.setHostIp(ip);
                    }
                });

        final Map<String, String> labels = container.labels();
        fillFromLabel(labels, MccyConstants.MCCY_LABEL_NAME, v -> {
            summary.setName(v);
        }, normalizeContainerName(container.names().get(0)));

        return summary;
    }

    public static ContainerSummary from(ContainerInfo info, String dockerHostIp, Optional<String> connectUsingHost) {
        ContainerSummary summary = new ContainerSummary();

        final Map<String, List<PortBinding>> ports = info.networkSettings().ports();
        // will be null when the container is stopped
        if (ports != null) {
            final List<PortBinding> portBindings = ports.get(MccyConstants.SERVER_CONTAINER_PORT);
            final PortBinding portBinding = portBindings.get(0);

            final String reportedHostIp = portBinding.hostIp();
            final String resolvedIp;
            if (connectUsingHost.isPresent()) {
                resolvedIp = connectUsingHost.get();
            }
            else if (reportedHostIp.equals(MccyConstants.IP_ADDR_ALL_IF)) {
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

        final Map<String, String> labels = info.config().labels();
        fillFromLabel(labels, MccyConstants.MCCY_LABEL_NAME, v -> {
            summary.setName(v);
        }, normalizeContainerName(info.name()));

        summary.setId(info.id());

        summary.setLabels(labels.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(MccyConstants.MCCY_LABEL_PREFIX))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        final String value = summary.getLabels().get(MccyConstants.MCCY_LABEL_MODPACK_URL);
        if (value != null) {
            summary.setModpack(URI.create(value));
        }

        return summary;
    }

    /**
     * The type of server, such as an official, vanilla server or one of the mod/plugin variants.
     */
    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String[] getOps() {
        return ops;
    }

    public void setOps(String[] ops) {
        this.ops = ops;
    }

    public String getForgeVersion() {
        return forgeVersion;
    }

    public void setForgeVersion(String forgeVersion) {
        this.forgeVersion = forgeVersion;
    }

    public String[] getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String[] whitelist) {
        this.whitelist = whitelist;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public Boolean getPvp() {
        return pvp;
    }

    public void setPvp(Boolean pvp) {
        this.pvp = pvp;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGeneratorSettings() {
        return generatorSettings;
    }

    public void setGeneratorSettings(String generatorSettings) {
        this.generatorSettings = generatorSettings;
    }

    public URI getWorld() {
        return world;
    }

    public void setWorld(URI world) {
        this.world = world;
    }

    private static void fillFromEnv(List<String> env, String envKey, Consumer<String> consumer) {
        final String prefix = envKey + "=";

        for (String e : env) {
            if (e.startsWith(prefix)) {
                consumer.accept(e.substring(prefix.length()));
            }
        }
    }

    private static void fillFromLabel(Map<String, String> labels, String labelKey, Consumer<String> consumer,
                                      String defaultValue) {
        final String value = labels.get(labelKey);
        consumer.accept(value != null ? value : defaultValue);
    }

    private static String normalizeContainerName(String fullName) {
        final int pos = fullName.lastIndexOf("/");
        if (pos >= 0) {
            return fullName.substring(pos + 1);
        }
        else {
            return fullName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
