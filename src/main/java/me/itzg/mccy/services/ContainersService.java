package me.itzg.mccy.services;

import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerInfo;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ContainerDetails;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.model.ContainerSummary;
import me.itzg.mccy.model.ServerStatus;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyUnexpectedServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.spotify.docker.client.DockerClient.ListContainersParam.allContainers;
import static com.spotify.docker.client.DockerClient.ListContainersParam.withLabel;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@Service
public class ContainersService {
    private static Logger LOG = LoggerFactory.getLogger(ContainersService.class);

    public static final String TRUE_VALUE = String.valueOf(true);

    @Autowired
    private DockerClientProxy proxy;

    @Autowired
    private ServerStatusService serverStatusService;

    @Autowired
    private ContainerBuilderService containerBuilderService;

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private MetadataConversionService metadataConversionService;

    public String create(ContainerRequest request, String ownerUsername, UriComponentsBuilder requestUri)
            throws MccyException, DockerException, InterruptedException {

        final ContainerConfig containerConfig = containerBuilderService.buildContainerConfig(request, ownerUsername, requestUri);

        LOG.debug("Creating the container: {}", containerConfig);

        return proxy.access(dockerClient -> {
            // Ensure latest image is always used
            dockerClient.pull(mccySettings.getImage());

            final String containerId = dockerClient
                    .createContainer(containerConfig, scrubContainerName(request.getName()))
                    .id();

            if (request.isStartOnCreate()) {
                dockerClient.startContainer(containerId);
            }

            return containerId;
        });

    }

    public static String scrubContainerName(String givenName) {
        return givenName.replaceAll("[^A-Za-z0-9]", "_");
    }

    public ServerStatus getContainerStatus(String containerId, String authUsername) throws DockerException, InterruptedException, TimeoutException, MccyUnexpectedServerException {
        final ContainerDetails containerDetails = get(containerId, authUsername);

        if (containerDetails != null) {
            final ContainerSummary summary = containerDetails.getSummary();
            return serverStatusService.queryStatus(summary.getHostIp(), summary.getHostPort());
        }
        else {
            return null;
        }
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

    /**
     * Obtains the details of a running container.
     *
     * @param containerId the Docker container ID
     * @param authUsername if null, only accesses public containers, otherwise this is used to narrow
     *                     access to only those containers owned by this given user
     * @return the details of the container
     * @throws DockerException
     * @throws InterruptedException
     */
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

    private boolean isOurs(ContainerInfo containerInfo) {
        return containerInfo.config().labels().containsKey(MccyConstants.MCCY_LABEL);
    }
}
