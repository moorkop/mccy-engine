package me.itzg.mccy.services;

import com.spotify.docker.client.DockerException;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public interface DockerClientProxy {
    <T> T access(DockerClientConsumer<T> consumer) throws DockerException, InterruptedException;
}
