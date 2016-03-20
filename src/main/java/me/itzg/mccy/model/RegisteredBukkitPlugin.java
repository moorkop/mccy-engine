package me.itzg.mccy.model;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class RegisteredBukkitPlugin extends RegisteredMod {
    private BukkitPluginInfo bukkitPluginInfo;

    public RegisteredBukkitPlugin() {
        super(new ServerType[]{ServerType.BUKKIT, ServerType.SPIGOT});
    }

    public BukkitPluginInfo getBukkitPluginInfo() {
        return bukkitPluginInfo;
    }

    public void setBukkitPluginInfo(BukkitPluginInfo bukkitPluginInfo) {
        this.bukkitPluginInfo = bukkitPluginInfo;
    }
}
