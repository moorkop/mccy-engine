package me.itzg.mccy.types;

import me.itzg.mccy.MccySwarmApplication;

/**
 * @author Geoff Bourne
 * @since 12/21/2015
 */
public class MccyConstants {

    public static final String MCCY_LABEL = MccySwarmApplication.class.getPackage().getName();
    public static final int SERVER_CONTAINER_PORT_INT = 25565;
    public static final String SERVER_CONTAINER_PORT = String.valueOf(SERVER_CONTAINER_PORT_INT)+ "/tcp";
    public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
}
