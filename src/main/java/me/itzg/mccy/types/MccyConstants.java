package me.itzg.mccy.types;

import me.itzg.mccy.MccySwarmApplication;

import java.nio.file.Path;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
public class MccyConstants {


    public static final String MCCY_LABEL_PREFIX = MccySwarmApplication.class.getPackage().getName();
    public static final String MCCY_LABEL = MCCY_LABEL_PREFIX;
    public static final String MCCY_LABEL_MODPACK_URL = MCCY_LABEL_PREFIX+".modpack-url";

    public static final int SERVER_CONTAINER_PORT_INT = 25565;
    public static final String SERVER_CONTAINER_PORT = String.valueOf(SERVER_CONTAINER_PORT_INT)+ "/tcp";
    public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";

    public static final String FILE_MCMOD_INFO = "mcmod.info";

    public static final String TEMP_PREFIX = "temp-";

    public static final String CATEGORY_WORLDS = "worlds";
    public static final String CATEGORY_MODS = "mods";

    public static final ComparableVersion FORGE_VERSION_CUTOFF = new ComparableVersion("1.8");

    public static final String EXT_WORLDS = ".zip";
    public static final String EXT_MODS = ".jar";
    public static final String EXT_MOD_PACK = ".zip";
    public static final String IP_ADDR_ALL_IF = "0.0.0.0";
}
