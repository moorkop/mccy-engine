package me.itzg.mccy.model;

import com.spotify.docker.client.messages.ContainerInfo;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class ContainerDetails {
    private ContainerSummary summary;
    private ContainerInfo info;

    public ContainerDetails(ContainerInfo info) {
        this.summary = new ContainerSummary();
        this.info = info;
    }

    public ContainerSummary getSummary() {
        return summary;
    }

    public void setSummary(ContainerSummary summary) {
        this.summary = summary;
    }

    public ContainerInfo getInfo() {
        return info;
    }

    public void setInfo(ContainerInfo info) {
        this.info = info;
    }

}
