package me.itzg.mccy.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class WebServerPortProviderImpl implements WebServerPortProvider,
        ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(WebServerPortProviderImpl.class);

    private int port = -1;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent embeddedServletContainerInitializedEvent) {
        this.port = embeddedServletContainerInitializedEvent.getEmbeddedServletContainer().getPort();
        LOG.debug("Discovered web container port to be {}", this.port);
    }
}
