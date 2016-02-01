package me.itzg.mccy.services;

import me.itzg.mccy.config.GeneralConfig;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ServerStatus;
import me.itzg.mccy.types.MccyUnexpectedServerException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 1/31/2016
 */
public class ServerStatusApp {
    public static void main(String[] args) throws TimeoutException, MccyUnexpectedServerException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Missing required parameters: host port");
        }

        final ServerStatusService service = new ServerStatusService();
        ReflectionTestUtils.setField(service, "mccySettings", new MccySettings());
        ReflectionTestUtils.setField(service, "remoteInvocationExecutor", new GeneralConfig().remoteInvocationExecutor());

        final ServerStatus serverStatus = service.queryStatus(args[0], Integer.parseInt(args[1]));

        System.out.println(serverStatus.toString());
    }

}
