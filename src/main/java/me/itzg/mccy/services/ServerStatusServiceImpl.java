package me.itzg.mccy.services;

import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.ServerStatus;
import me.itzg.mccy.types.MccyUnexpectedServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.data.SubProtocol;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoHandler;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.tcp.TcpSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Geoff Bourne
 * @since 1/31/2016
 */
@Service
public class ServerStatusServiceImpl implements ServerStatusService {
    private static Logger LOG = LoggerFactory.getLogger(ServerStatusServiceImpl.class);

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private ConcurrentTaskExecutor remoteInvocationExecutor;

    @Override
    public ServerStatus queryStatus(String host, int port) throws TimeoutException, MccyUnexpectedServerException {

        final CompletableFuture<ServerStatus> completableFuture = new CompletableFuture<>();

        final Future<Boolean> invocationFuture = remoteInvocationExecutor.submit(() -> {
            MinecraftProtocol protocol = new MinecraftProtocol(SubProtocol.STATUS);
            Client client = new Client(host, port, protocol, new TcpSessionFactory(Proxy.NO_PROXY));
            client.getSession().setFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
            client.getSession().setFlag(MinecraftConstants.SERVER_INFO_HANDLER_KEY, new ServerInfoHandler() {
                @Override
                public void handle(Session session, ServerStatusInfo info) {

                    final ServerStatus status = new ServerStatus();
                    status.setReportedVersion(info.getVersionInfo().getVersionName());
                    status.setOnlinePlayers(info.getPlayerInfo().getOnlinePlayers());
                    status.setMaxPlayers(info.getPlayerInfo().getMaxPlayers());
                    status.setReportedDescription(info.getDescription().getFullText());

                    final ByteArrayOutputStream iconBytesOut = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(info.getIcon(), "png", iconBytesOut);
                        status.setIcon(iconBytesOut.toByteArray());
                    } catch (IOException e) {
                        LOG.warn("Failed to write image bytes of server icon {} from {}:{}", e, info.getIcon(), host, port);
                    }

                    completableFuture.complete(status);
                }
            });

            client.getSession().connect();

            while(client.getSession().isConnected()) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    return false;
                }
            }
            return true;
        });

        try {
            return completableFuture.get(mccySettings.getServerStatusTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new MccyUnexpectedServerException(e);
        }
    }

}
