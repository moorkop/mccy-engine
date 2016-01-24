package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 1/24/2016
 */
@Component
@ConfigurationProperties("mccy.version")
public class MccyVersionSettings {

    /**
     * This refers to the official versions.json Minecraft catalog.
     */
    private URI officialVersionsUri = URI.create("https://s3.amazonaws.com/Minecraft.Download/versions/versions.json");

    /**
     * The amount of time (in minutes) to cache the official versions REST API lookup.
     */
    private long officialVersionsCacheTime = 5;

    /**
     * There is limited support of Bukkit in the itzg/minecraft-server image, so the supported versions need to
     * explicitly defined.
     */
    private String[] bukkitVersions = new String[]{"1.8"};

    /**
     * Since the Bukkit plugin.yml itself doesn't declare the support Minecraft game version, we need to start
     * with a reasonable default.
     */
    private String defaultBukkitGameVersion = "1.8";

    public URI getOfficialVersionsUri() {
        return officialVersionsUri;
    }

    public void setOfficialVersionsUri(URI officialVersionsUri) {
        this.officialVersionsUri = officialVersionsUri;
    }

    public long getOfficialVersionsCacheTime() {
        return officialVersionsCacheTime;
    }

    public void setOfficialVersionsCacheTime(long officialVersionsCacheTime) {
        this.officialVersionsCacheTime = officialVersionsCacheTime;
    }

    public String[] getBukkitVersions() {
        return bukkitVersions;
    }

    public void setBukkitVersions(String[] bukkitVersions) {
        this.bukkitVersions = bukkitVersions;
    }

    public String getDefaultBukkitGameVersion() {
        return defaultBukkitGameVersion;
    }

    public void setDefaultBukkitGameVersion(String defaultBukkitGameVersion) {
        this.defaultBukkitGameVersion = defaultBukkitGameVersion;
    }
}
