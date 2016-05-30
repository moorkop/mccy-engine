package me.itzg.mccy.services;

import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import me.itzg.mccy.model.ContainerRequest;
import me.itzg.mccy.types.MccyException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface ContainerBuilderService {
    ContainerConfig buildContainerConfig(ContainerRequest request, String ownerUsername,
                                         UriComponentsBuilder requestUri)
            throws MccyException, DockerException, InterruptedException;
}
