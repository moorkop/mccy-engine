package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
@Component
@ConfigurationProperties("mccy")
public class MccySettings {

    @Size(min = 1)
    private String dockerCertPath;

    @NotNull(message = "The DOCKER_HOST URI is required")
    @Pattern(regexp = "https?://.*", message = "The DOCKER_HOST URI must be HTTP or HTTPS")
    private String dockerHostUri;

    @NotNull
    private String image = "itzg/minecraft-server";

    @Min(1)
    private int secondsToWaitOnStop = 30;

    /**
     * This can be set to override the default derivation of the file-access URL
     */
    private URI externalUri;

    /**
     * This refers to the official versions.json Minecraft catalog.
     */
    private URI officialVersionsUri = URI.create("https://s3.amazonaws.com/Minecraft.Download/versions/versions.json");

    public String getDockerCertPath() {
        return dockerCertPath;
    }

    public void setDockerCertPath(String dockerCertPath) {
        this.dockerCertPath = dockerCertPath;
    }

    public String getDockerHostUri() {
        return dockerHostUri;
    }

    public void setDockerHostUri(String dockerHostUri) {
        this.dockerHostUri = dockerHostUri;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSecondsToWaitOnStop() {
        return secondsToWaitOnStop;
    }

    public void setSecondsToWaitOnStop(int secondsToWaitOnStop) {
        this.secondsToWaitOnStop = secondsToWaitOnStop;
    }

    public URI getExternalUri() {
        return externalUri;
    }

    public void setExternalUri(URI externalUri) {
        this.externalUri = externalUri;
    }

    public URI getOfficialVersionsUri() {
        return officialVersionsUri;
    }

    public void setOfficialVersionsUri(URI officialVersionsUri) {
        this.officialVersionsUri = officialVersionsUri;
    }
}
