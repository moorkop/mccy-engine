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
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private static Logger LOG = LoggerFactory.getLogger(ContainersService.class);

    //TODO replace this approach with some AOP magic
    @Autowired
    private DockerClientProxy proxy;

    @Autowired
    private MccySettings mccySettings;

    public String create(ContainerRequest request) throws MccyException, DockerException, InterruptedException {

        PortBinding portBinding = PortBinding.of("0.0.0.0", request.getPort());
        Map<String, List<PortBinding>> portBindings =
                singletonMap(MccyConstants.SERVER_CONTAINER_PORT, singletonList(portBinding));

        HostConfig hostConfig = HostConfig.builder()
                .portBindings(portBindings)
                .build();
        final ContainerConfig config = ContainerConfig.builder()
                .attachStdin(true)
                .tty(true)
                .env(fillEnv(request))
                .image(mccySettings.getImage())
                .hostConfig(hostConfig)
                .labels(buildLabels(request))
                .build();

        LOG.debug("Creating the container: {}", config);

        return proxy.access(dockerClient -> {
            // Ensure latest image is always used
            dockerClient.pull(mccySettings.getImage());

            final String containerId = dockerClient.createContainer(config, request.getName()).id();

            if (request.isStartOnCreate()) {
                dockerClient.startContainer(containerId);
            }

            return containerId;
        });
    }

    public List<ContainerSummary> getAll() throws DockerException, InterruptedException {
        return proxy.access(dockerClient -> {
            //noinspection CodeBlock2Expr
            return dockerClient.listContainers(allContainers(), withLabel(MccyConstants.MCCY_LABEL))
                    .stream().map(c -> ContainerSummary.from(c, getDockerHostIp()))
                    .collect(Collectors.toList());
        });
    }

    private String getDockerHostIp() {
        return URI.create(mccySettings.getDockerHostUri()).getHost();
    }

    public ContainerDetails get(String containerId) throws DockerException, InterruptedException {
        return proxy.access(dockerClient -> {
            final ContainerInfo containerInfo = dockerClient.inspectContainer(containerId);
            if (isOurs(containerInfo)) {
                final ContainerDetails containerDetails = new ContainerDetails(containerInfo);

                containerDetails.setSummary(ContainerSummary.from(containerInfo,
                        URI.create(mccySettings.getDockerHostUri()).getHost()));

                return containerDetails;
            } else {
                return null;
            }
        });
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

    protected Map<String, String> buildLabels(ContainerRequest request) {
        Map<String, String> labels = new HashMap<>();
        labels.put(MccyConstants.MCCY_LABEL, String.valueOf(true));

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
        fillStringInEnv(env, request.getWorld(), MccyConstants.ENV_WORLD);
        fillStringInEnv(env, request.getModpack(), MccyConstants.ENV_MODPACK);

        final ServerType type = request.getType();
        if (type != null) {
            addToEnv(env, "TYPE", type.name());
        }

        fillPlayerList(env, request.getWhitelist(), "WHITELIST");
        fillPlayerList(env, request.getOps(), "OPS");

        return env;
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
