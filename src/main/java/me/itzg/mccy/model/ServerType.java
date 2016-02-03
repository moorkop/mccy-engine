package me.itzg.mccy.model;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public enum ServerType {
    VANILLA(true),
    SNAPSHOT(true),
    FORGE(false),
    BUKKIT(false, true),
    SPIGOT(false, true);

    private final boolean official;
    private final boolean bukkitCompatible;

    ServerType(boolean official) {

        this.official = official;
        this.bukkitCompatible = false;
    }


    ServerType(boolean official, boolean bukkitCompatible) {

        this.official = official;
        this.bukkitCompatible = bukkitCompatible;
    }

    public boolean isOfficial() {
        return official;
    }

    public boolean isBukkitCompatible() {
        return bukkitCompatible;
    }
}
