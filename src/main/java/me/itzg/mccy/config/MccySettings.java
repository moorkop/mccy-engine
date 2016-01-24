package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.Optional;

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
    @Pattern(regexp = "(https?|unix)://.*", message = "The DOCKER_HOST URI scheme must be http, https, or unix")
    private String dockerHostUri;

    @NotNull
    private String image = "itzg/minecraft-server";

    @Min(1)
    private int secondsToWaitOnStop = 30;

    /**
     * Declares what specific settings will be exposed by the /api/settings endpoint
     */
    private String[] uiVisibleSettings;

    /**
     * This can be set to override the default derivation of the file-access URL
     */
    private URI externalUri;

    /**
     * This indicates if a Docker link should be established and used from the minecraft-server
     * container to the MCCY container to download world and mod content.
     */
    private boolean usingLinkForContent = false;

    /**
     * When set, this will always be used to derive the "connect using" address of the Minecraft
     * server. This is primarily useful for a <code>dockerHostUri</code> in the <code>unix://</code>
     * scheme, since the actual Docker host's address is not easily identifiable.
     */
    private Optional<String> connectUsingHost = Optional.empty();

    /**
     * When set, this conveys a shout out to the deployment provider.
     */
    @Valid
    private DeploymentPoweredBy deploymentPoweredBy = null;

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

    public String[] getUiVisibleSettings() {
        return uiVisibleSettings;
    }

    public void setUiVisibleSettings(String[] uiVisibleSettings) {
        this.uiVisibleSettings = uiVisibleSettings;
    }

    public Optional<String> getConnectUsingHost() {
        return connectUsingHost;
    }

    public void setConnectUsingHost(Optional<String> connectUsingHost) {
        this.connectUsingHost = connectUsingHost;
    }

    public DeploymentPoweredBy getDeploymentPoweredBy() {
        return deploymentPoweredBy;
    }

    public void setDeploymentPoweredBy(DeploymentPoweredBy deploymentPoweredBy) {
        this.deploymentPoweredBy = deploymentPoweredBy;
    }

    public boolean isUsingLinkForContent() {
        return usingLinkForContent;
    }

    public void setUsingLinkForContent(boolean usingLinkForContent) {
        this.usingLinkForContent = usingLinkForContent;
    }

    public static class DeploymentPoweredBy {
        @NotNull @Size(min = 1)
        private String imageSrc;

        private URI href;

        public String getImageSrc() {
            return imageSrc;
        }

        public void setImageSrc(String imageSrc) {
            this.imageSrc = imageSrc;
        }

        public URI getHref() {
            return href;
        }

        public void setHref(URI href) {
            this.href = href;
        }
    }
}
