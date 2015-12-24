package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@ConfigurationProperties("mccy.world")
@Component
public class MccyWorldSettings {
    private int htmlPeekLimit = 100;

    public int getHtmlPeekLimit() {
        return htmlPeekLimit;
    }

    public void setHtmlPeekLimit(int htmlPeekLimit) {
        this.htmlPeekLimit = htmlPeekLimit;
    }
}
