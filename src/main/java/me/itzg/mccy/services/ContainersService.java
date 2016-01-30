package me.itzg.mccy.services;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ContainerDetails;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.model.ContainerSummary;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static com.spotify.docker.client.DockerClient.ListContainersParam.allContainers;
import static com.spotify.docker.client.DockerClient.ListContainersParam.withLabel;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@Service
public class ContainersService {
    public static final String TRUE_VALUE = String.valueOf(true);
    private static Logger LOG = LoggerFactory.getLogger(ContainersService.class);

    //TODO replace this approach with some AOP magic
    @Autowired
    private DockerClientProxy proxy;

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private String ourContainerId;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Optional<EmbeddedWebApplicationContext> embeddedWebApplicationContext;

    @Autowired
    private MetadataConversionService metadataConversionService;

    public String create(ContainerRequest request, String ownerUsername) throws MccyException, DockerException, InterruptedException {

        final int requestedPort = request.getPort();
        PortBinding portBinding = PortBinding.of("",
                requestedPort != 0 ? String.valueOf(requestedPort) : "");
        Map<String, List<PortBinding>> portBindings =
                singletonMap(MccyConstants.SERVER_CONTAINER_PORT, singletonList(portBinding));

        final HostConfig.Builder hostConfig = HostConfig.builder()
                .portBindings(portBindings);

        if (ourContainerId != null && needsLink(request)) {
            hostConfig.links(ourContainerId+":"+ MccyConstants.LINK_MCCY);
        }

        final ContainerConfig config = ContainerConfig.builder()
                .attachStdin(true)
                .tty(true)
                .exposedPorts(MccyConstants.SERVER_CONTAINER_PORT)
                .env(fillEnv(request))
                .image(mccySettings.getImage())
                .hostConfig(hostConfig.build())
                .labels(buildLabels(request, ownerUsername))
                .build();

        LOG.debug("Creating the container: {}", config);

        return proxy.access(dockerClient -> {
            // Ensure latest image is always used
            dockerClient.pull(mccySettings.getImage());

            final String containerId = dockerClient
                    .createContainer(config, scrubContainerName(request.getName()))
                    .id();

            if (request.isStartOnCreate()) {
                dockerClient.startContainer(containerId);
            }

            return containerId;
        });
    }

    private boolean needsLink(ContainerRequest request) {
        return mccySettings.isUsingLinkForContent() &&
                (request.getModpack() != null || request.getWorld() != null);
    }

    public static String scrubContainerName(String givenName) {
        return givenName.replaceAll("[^A-Za-z0-9]", "_");
    }

    public List<ContainerSummary> getAll(String ownedByUsername) throws DockerException, InterruptedException {
        return proxy.access(dockerClient -> {
            //noinspection CodeBlock2Expr
            return dockerClient
                    .listContainers(allContainers(),
                            withLabel(MccyConstants.MCCY_LABEL))
                    .stream()
                    .filter(c -> {
                        final String owner = c.labels().get(MccyConstants.MCCY_LABEL_OWNER);
                        // for backward compatibility, unowned containers are also matched
                        return owner == null || owner.equals(ownedByUsername);
                    })
                    .map(c -> ContainerSummary.from(c,
                            getDockerHostIp(), mccySettings.getConnectUsingHost()))
                    .collect(Collectors.toList());
        });
    }

    public List<ContainerSummary> getAllPublic() throws DockerException, InterruptedException {
        return proxy.access(dockerClient -> {
            //noinspection CodeBlock2Expr
            return dockerClient
                    .listContainers(allContainers(),
                    withLabel(MccyConstants.MCCY_LABEL_PUBLIC, TRUE_VALUE))
                    .stream().map(c -> ContainerSummary.from(c,
                            getDockerHostIp(), mccySettings.getConnectUsingHost()))
                    .collect(Collectors.toList());
        });
    }

    private String getDockerHostIp() {
        return URI.create(mccySettings.getDockerHostUri()).getHost();
    }

    public ContainerDetails get(String containerId, String authUsername) throws DockerException, InterruptedException {
        return proxy.access(dockerClient -> {
            final ContainerInfo containerInfo = dockerClient.inspectContainer(containerId);
            if (isOurs(containerInfo) && canRead(containerInfo, authUsername)) {
                final ContainerDetails containerDetails = new ContainerDetails(containerInfo);

                final ContainerSummary summary = ContainerSummary.from(containerInfo,
                        getDockerHostIp(), mccySettings.getConnectUsingHost());

                metadataConversionService.fillFromEnv(containerInfo.config().env(), summary);

                containerDetails.setSummary(summary);

                return containerDetails;
            } else {
                return null;
            }
        });
    }

    private boolean canRead(ContainerInfo containerInfo, String authUsername) {
        if (authUsername == null) {
            final String publicVal = containerInfo.config().labels().get(MccyConstants.MCCY_LABEL_PUBLIC);
            return publicVal != null && Boolean.parseBoolean(publicVal);
        }
        else {
            final String ownerVal = containerInfo.config().labels().get(MccyConstants.MCCY_LABEL_OWNER);
            return ownerVal != null && ownerVal.equals(authUsername);
        }
    }

    public void delete(String containerId) throws DockerException, InterruptedException {
        proxy.access(dockerClient -> {
            final ContainerInfo containerInfo = dockerClient.inspectContainer(containerId);

            if (!isOurs(containerInfo)) {
                throw new IllegalArgumentException("The given container is not managed by us");
            }

            if (containerInfo.state().running()) {
                dockerClient.stopContainer(containerId, mccySettings.getSecondsToWaitOnStop());
            }

            dockerClient.removeContainer(containerId);

            return null;
        });
    }

    public void start(String containerId) throws DockerException, InterruptedException {
        proxy.access(dockerClient -> {
            final ContainerInfo containerInfo = dockerClient.inspectContainer(containerId);

            if (isOurs(containerInfo)) {
                if (containerInfo.state().running()) {
                    throw new IllegalArgumentException("Container is already running");
                }
                dockerClient.startContainer(containerId);
            } else {
                throw new IllegalArgumentException("The given container is not managed by us");
            }

            return null;
        });
    }

    public void stop(String containerId) throws DockerException, InterruptedException {
        proxy.access(dockerClient -> {
            final ContainerInfo containerInfo = dockerClient.inspectContainer(containerId);

            if (isOurs(containerInfo)) {
                if (!containerInfo.state().running()) {
                    throw new IllegalArgumentException("Container was not running");
                }
                dockerClient.stopContainer(containerId, mccySettings.getSecondsToWaitOnStop());
            } else {
                throw new IllegalArgumentException("The given container is not managed by us");
            }

            return null;
        });
    }

    protected Map<String, String> buildLabels(ContainerRequest request, String ownerUsername) {
        Map<String, String> labels = new HashMap<>();
        labels.put(MccyConstants.MCCY_LABEL, TRUE_VALUE);
        // store the name as provided by user
        labels.put(MccyConstants.MCCY_LABEL_NAME, request.getName());
        labels.put(MccyConstants.MCCY_LABEL_OWNER, ownerUsername);
        if (request.isVisibleToPublic()) {
            labels.put(MccyConstants.MCCY_LABEL_PUBLIC, TRUE_VALUE);
        }

        if (!Strings.isNullOrEmpty(request.getModpack())) {
            labels.put(MccyConstants.MCCY_LABEL_MODPACK_URL, request.getModpack());
        }

        return labels;
    }

    protected List<String> fillEnv(ContainerRequest request) {
        final ArrayList<String> env = new ArrayList<>();

        if (request.isAckEula()) {
            addToEnv(env, "EULA", "TRUE");
        }

        fillStringInEnv(env, request.getVersion(), MccyConstants.ENV_VERSION);
        fillStringInEnv(env, request.getIcon(), MccyConstants.ENV_ICON);

        if (ourContainerId != null && mccySettings.isUsingLinkForContent()) {
            fillLinkedUriInEnv(env, request.getWorld(), MccyConstants.ENV_WORLD);
            fillLinkedUriInEnv(env, request.getModpack(), MccyConstants.ENV_MODPACK);
        }
        else {
            fillStringInEnv(env, request.getWorld(), MccyConstants.ENV_WORLD);
            fillStringInEnv(env, request.getModpack(), MccyConstants.ENV_MODPACK);
        }

        final ServerType type = request.getType();
        if (type != null) {
            addToEnv(env, "TYPE", type.name());
        }

        fillPlayerList(env, request.getWhitelist(), "WHITELIST");
        fillPlayerList(env, request.getOps(), "OPS");

        return env;
    }

    private void fillLinkedUriInEnv(ArrayList<String> env, String originalUri, String varName) {
        if (originalUri == null) {
            return;
        }

        final String viaLink = UriComponentsBuilder.fromHttpUrl(originalUri)
                .host(MccyConstants.LINK_MCCY)
                .port(getOurPort())
                .build().toUriString();
        fillStringInEnv(env, viaLink, varName);
    }

    private int getOurPort() {
        return embeddedWebApplicationContext.orElseThrow(IllegalStateException::new)
                .getEmbeddedServletContainer().getPort();
    }

    protected void addToEnv(ArrayList<String> env, String key, Object value) {
        if (value != null) {
            env.add(String.format("%s=%s", key, value.toString()));
        }
    }

    private boolean isOurs(ContainerInfo containerInfo) {
        return containerInfo.config().labels().containsKey(MccyConstants.MCCY_LABEL);
    }

    private void fillStringInEnv(ArrayList<String> env, String value, String envKey) {
        if (!Strings.isNullOrEmpty(value)) {
            addToEnv(env, envKey, value);
        }
    }

    private void fillPlayerList(ArrayList<String> env, List<String> playerList, String envKey) {
        if (playerList != null && !playerList.isEmpty()) {
            addToEnv(env, envKey, Joiner.on(",").join(playerList));
        }
    }
}
