package me.itzg.mccy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@ConfigurationProperties("mccy.files")
@Component
public class MccyFilesSettings {
    /**
     * The directory where the file repo service will store its files. A relative path will
     * end up being relative to the working directory.
     */
    private String repoDir = "repo";

    public String getRepoDir() {
        return repoDir;
    }

    public void setRepoDir(String repoDir) {
        this.repoDir = repoDir;
    }
}
