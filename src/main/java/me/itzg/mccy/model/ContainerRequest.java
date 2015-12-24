package me.itzg.mccy.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
public class ContainerRequest {
    @AssertTrue(message = "The Minecraft EULA needs to be accepted for each created container")
    private boolean ackEula;

    @NotNull @Size(min = 1)
    private String name;

    @Min(value = 25565, message = "Minecraft server ports need to be 25565 (the default) or greater")
    private int port;

    private String version;

    private ServerType type;

    private List<String> whitelist;

    private List<String> ops;

    @Pattern(regexp = "https?://.+", message = "needs to be a valid http or https URL")
    private String icon;

    @Pattern(regexp = "https?://.+", message = "needs to be a valid http or https URL")
    private String world;

    private boolean startOnCreate = true;

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

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
}
