package me.itzg.mccy.config;

import me.itzg.mccy.types.ValidViaFixedUriSettings;
import me.itzg.mccy.types.ValidViaNetworkSettings;
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
@ValidViaNetworkSettings
@ValidViaFixedUriSettings
public class MccyAssetSettings {

    @NotNull
    private File storageDir;

    @NotNull
    private Via via = Via.REQUEST_URL;

    private String network;

    private String myNameOnNetwork;

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

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public URI getFixedUri() {
        return fixedUri;
    }

    @SuppressWarnings("unused")
    public void setFixedUri(URI fixedUri) {
        this.fixedUri = fixedUri;
    }

    public String getMyNameOnNetwork() {
        return myNameOnNetwork;
    }

    public void setMyNameOnNetwork(String myNameOnNetwork) {
        this.myNameOnNetwork = myNameOnNetwork;
    }

    public enum Via {
        NETWORK,
        LINK,
        FIXED_URI,
        REQUEST_URL
    }
}
