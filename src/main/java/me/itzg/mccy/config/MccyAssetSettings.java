package me.itzg.mccy.config;

import me.itzg.mccy.types.ValidFixedUriSettings;
import me.itzg.mccy.types.ValidOverlaySettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.URI;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@ConfigurationProperties("mccy.assets")
@Component
@ValidOverlaySettings
@ValidFixedUriSettings
public class MccyAssetSettings {

    @NotNull
    private File storageDir;

    @NotNull
    private Via via = Via.REQUEST_URL;

    private String overlayNetwork;

    private String myOverlayName;

    private URI fixedUri;

    public Via getVia() {
        return via;
    }

    public void setVia(Via via) {
        this.via = via;
    }

    public File getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(File storageDir) {
        this.storageDir = storageDir;
    }

    public String getOverlayNetwork() {
        return overlayNetwork;
    }

    public void setOverlayNetwork(String overlayNetwork) {
        this.overlayNetwork = overlayNetwork;
    }

    public URI getFixedUri() {
        return fixedUri;
    }

    @SuppressWarnings("unused")
    public void setFixedUri(URI fixedUri) {
        this.fixedUri = fixedUri;
    }

    public String getMyOverlayName() {
        return myOverlayName;
    }

    public void setMyOverlayName(String myOverlayName) {
        this.myOverlayName = myOverlayName;
    }

    public enum Via {
        OVERLAY,
        LINK,
        FIXED_URI,
        REQUEST_URL
    }
}
