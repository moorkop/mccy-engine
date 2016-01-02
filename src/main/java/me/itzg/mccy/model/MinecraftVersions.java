package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by geoff on 12/27/15.
 */
public class MinecraftVersions {
    public Collection<VersionEntry> getVersions() {
        return versions;
    }

    public void setVersions(Collection<VersionEntry> versions) {
        this.versions = versions;
    }

    public Map<Type, String> getLatest() {
        return latest;
    }

    public void setLatest(Map<Type, String> latest) {
        this.latest = latest;
    }

    public enum Type {
        release(true),
        snapshot(true),
        old_beta(true),
        old_alpha(true),
        forge(false);

        private final boolean official;

        Type(boolean official) {
            this.official = official;
        }

        public boolean isOfficial() {
            return official;
        }
    }

    private Map<Type, String> latest;

    @JsonUnwrapped
    private Collection<VersionEntry> versions;

    public static class VersionEntry {
        private String id;
        private Date time;
        private Date releaseTime;
        private Type type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public Date getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(Date releaseTime) {
            this.releaseTime = releaseTime;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }
}
