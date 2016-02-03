package me.itzg.mccy.model;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class RegisteredBukkitPlugin extends RegisteredMod {
    private BukkitPluginInfo info;

    public RegisteredBukkitPlugin() {
        super(new ServerType[]{ServerType.BUKKIT, ServerType.SPIGOT});
    }

    public BukkitPluginInfo getInfo() {
        return info;
    }

    public void setInfo(BukkitPluginInfo info) {
        this.info = info;
    }
}
