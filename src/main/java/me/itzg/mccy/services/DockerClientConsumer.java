package me.itzg.mccy.services;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;

/**
 * @author Geoff Bourne
 * @since 1/30/2016
 */
public interface DockerClientConsumer<T> {
    T use(DockerClient dockerClient) throws DockerException, InterruptedException;
}
