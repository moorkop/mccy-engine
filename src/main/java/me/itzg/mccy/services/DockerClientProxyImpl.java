package me.itzg.mccy.services;

import com.google.common.base.Optional;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerCertificates;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import me.itzg.mccy.config.MccySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
@Service @Primary
public class DockerClientProxyImpl implements DockerClientProxy {
    private MccySettings mccySettings;
    private DockerClient dockerClient;

    @Override
    public <T> T access(DockerClientConsumer<T> consumer) throws DockerException, InterruptedException {

        return consumer.use(dockerClient);
    }

    @Autowired
    public void setMccySettings(MccySettings mccySettings) {
        this.mccySettings = mccySettings;

        this.dockerClient = createClient();
    }

    private DefaultDockerClient createClient() {
        DefaultDockerClient dockerClient;
        Optional<DockerCertificates> certs = Optional.absent();
        if (mccySettings.getDockerCertPath() != null) {
            try {
                certs = DockerCertificates.builder()
                        .dockerCertPath(Paths.get(mccySettings.getDockerCertPath())).build();
            } catch (DockerCertificateException e) {
                throw new IllegalArgumentException("Unable to initialize Docker certificates with given configuration");
            }
        }

        final DefaultDockerClient.Builder clientBuilder = DefaultDockerClient.builder();

        if (certs.isPresent()) {
            clientBuilder
                    .dockerCertificates(certs.get());
        }

        dockerClient = clientBuilder
                .uri(mccySettings.getDockerHostUri())
                .build();
        return dockerClient;
    }
}
