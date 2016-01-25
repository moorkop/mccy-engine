package me.itzg.mccy.types;

import me.itzg.mccy.MccySwarmApplication;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
public class MccyConstants {


    public static final String MCCY_LABEL_PREFIX = MccySwarmApplication.class.getPackage().getName();
    public static final String MCCY_LABEL = MCCY_LABEL_PREFIX;
    public static final String MCCY_LABEL_MODPACK_URL = MCCY_LABEL_PREFIX+".modpack-url";
    public static final String MCCY_LABEL_NAME = MCCY_LABEL_PREFIX+".name";
    public static final String MCCY_LABEL_PUBLIC = MCCY_LABEL_PREFIX+".public";
    public static final String MCCY_LABEL_OWNER = MCCY_LABEL_PREFIX+".owner";

    public static final int SERVER_CONTAINER_PORT_INT = 25565;
    public static final String SERVER_CONTAINER_PORT = String.valueOf(SERVER_CONTAINER_PORT_INT)+ "/tcp";
    public static final String IP_ADDR_ALL_IF = "0.0.0.0";

    public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";

    public static final String FILE_MCMOD_INFO = "mod.info";
    public static final String FILE_PLUGIN_META = "plugin.yml";

    public static final String TEMP_PREFIX = "temp-";

    public static final String CATEGORY_WORLDS = "worlds";
    public static final String CATEGORY_MODS = "mods";

    public static final ComparableVersion FORGE_VERSION_CUTOFF = new ComparableVersion("1.8");

    public static final String EXT_WORLDS = ".zip";
    public static final String EXT_MODS = ".jar";
    public static final String EXT_MOD_PACK = ".zip";

    public static final String ENV_ICON = "ICON";
    public static final String ENV_VERSION = "VERSION";
    public static final String ENV_FORGEVERSION = "FORGEVERSION";
    public static final String ENV_WORLD = "WORLD";
    public static final String ENV_MODPACK = "MODPACK";
    public static final String ENV_TYPE = "TYPE";
    public static final String ENV_DIFFICULTY = "DIFFICULTY";
    public static final String ENV_WHITELIST = "WHITELIST";
    public static final String ENV_OPS = "OPS";
    public static final String ENV_SEED = "SEED";
    public static final String ENV_MODE = "MODE";
    public static final String ENV_MOTD = "MOTD";
    public static final String ENV_PVP = "PVP";
    public static final String ENV_LEVEL_TYPE = "LEVEL_TYPE";
    public static final String ENV_GENERATOR_SETTINGS = "GENERATOR_SETTINGS";
    public static final String ENV_LEVEL = "LEVEL";

    public static final String LINK_MCCY = "mccy";
}
