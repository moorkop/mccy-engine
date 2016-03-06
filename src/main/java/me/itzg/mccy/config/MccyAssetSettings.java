package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@ConfigurationProperties("mccy.assets")
@Component
public class MccyAssetSettings {
    @NotNull
    private File storageDir;

    public File getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(File storageDir) {
        this.storageDir = storageDir;
    }
}
