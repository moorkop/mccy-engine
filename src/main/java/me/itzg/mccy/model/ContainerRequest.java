package me.itzg.mccy.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class ContainerRequest {
    @AssertTrue(message = "The Minecraft EULA needs to be accepted for each created container")
    private boolean ackEula;

    @NotNull @Size(min = 1)
    private String name;

    @Min(value = 0, message = "Port needs to be 0 or greater")
    private int port;

    private String version;

    private ServerType type;

    private List<String> whitelist;

    private List<String> ops;

    @Pattern(regexp = "https?://.+", message = "needs to be a valid http or https URL")
    private String icon;

    @Pattern(regexp = "https?://.+", message = "needs to be a valid http or https URL")
    private String modpack;

    private boolean startOnCreate = true;

    private boolean visibleToPublic;

    private List<AssetRef> assets;

    public boolean isAckEula() {
        return ackEula;
    }

    public void setAckEula(boolean ackEula) {
        this.ackEula = ackEula;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStartOnCreate() {
        return startOnCreate;
    }

    public void setStartOnCreate(boolean startOnCreate) {
        this.startOnCreate = startOnCreate;
    }

    /**
     * A specific Minecraft version or the special values <code>LATEST</code> or <code>SNAPSHOT</code>
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param version a specific Minecraft version or the special values <code>LATEST</code> or <code>SNAPSHOT</code>
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public List<String> getOps() {
        return ops;
    }

    public void setOps(List<String> ops) {
        this.ops = ops;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Refers to either a zip of Forge mods or a zip of Bukkit/Spigot plugins, but generalizes
     * on the term "mod pack"
     */
    public String getModpack() {
        return modpack;
    }

    public void setModpack(String modpack) {
        this.modpack = modpack;
    }

    /**
     * Indicates if the container's summary should be visible to public (non-authenticated) users
     * of the site. These containers will be visible on the landing page.
     * @return true if the container is visible to the public
     */
    public boolean isVisibleToPublic() {
        return visibleToPublic;
    }

    public void setVisibleToPublic(boolean visibleToPublic) {
        this.visibleToPublic = visibleToPublic;
    }

    /**
     * Specifies the assets to be used by this container.
     * @return the assets
     */
    public List<AssetRef> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetRef> assets) {
        this.assets = assets;
    }
}
