package me.itzg.mccy.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.itzg.mccy.types.ComparableVersion;
import me.itzg.mccy.types.MinecraftVersionDeserializer;

import java.util.List;

/**
 * This conveys the essential fields of a Minecraft world/save/level in an abstract way across versions of
 * Minecraft and types of Minecraft servers.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
public class LevelDescriptor {
    private String name;

    private ComparableVersion minecraftVersion;

    private ServerType serverType;

    private List<ModRef> requiredMods;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Indicates the version of Minecraft needed to properly use this level.
     * <p>NOTE: prior to 1.9 the level data did not indicate the specific Minecraft version, so a heuristic will
     * be used in those cases to initial derive this version.</p>
     *
     * @return
     */
    public ComparableVersion getMinecraftVersion() {
        return minecraftVersion;
    }

    @JsonDeserialize(using = MinecraftVersionDeserializer.class)
    public void setMinecraftVersion(ComparableVersion minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public List<ModRef> getRequiredMods() {
        return requiredMods;
    }

    public void setRequiredMods(List<ModRef> requiredMods) {
        this.requiredMods = requiredMods;
    }
}
