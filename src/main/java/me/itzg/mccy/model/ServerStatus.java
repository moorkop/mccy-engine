package me.itzg.mccy.model;

import com.google.common.base.MoreObjects;

import java.awt.image.BufferedImage;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class ServerStatus {

    private String reportedVersion;
    private int onlinePlayers;
    private int maxPlayers;
    private String reportedDescription;
    private byte[] icon;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("reportedVersion", reportedVersion)
                .add("onlinePlayers", onlinePlayers)
                .add("maxPlayers", maxPlayers)
                .add("reportedDescription", reportedDescription)
                .add("icon", icon != null ? String.format("(%d bytes)", icon.length) : "null")
                .toString();
    }

    public void setReportedVersion(String reportedVersion) {
        this.reportedVersion = reportedVersion;
    }

    public String getReportedVersion() {
        return reportedVersion;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setReportedDescription(String reportedDescription) {
        this.reportedDescription = reportedDescription;
    }

    public String getReportedDescription() {
        return reportedDescription;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public byte[] getIcon() {
        return icon;
    }
}
