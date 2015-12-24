package me.itzg.mccy.model;

import me.itzg.mccy.config.MccySettings;

import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 12/22/2015
 */
public class ApplicationInfo {
    private URI dockerHostUri;

    public URI getDockerHostUri() {
        return dockerHostUri;
    }

    public void setDockerHostUri(URI dockerHostUri) {
        this.dockerHostUri = dockerHostUri;
    }

    public static ApplicationInfo from(MccySettings mccySettings) {
        ApplicationInfo me = new ApplicationInfo();
        me.dockerHostUri = URI.create(mccySettings.getDockerHostUri());
        return me;
    }
}
