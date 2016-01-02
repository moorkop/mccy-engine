package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 12/31/2015
 */
public class UploadModsResponse {
    private List<RegisteredMod> mods = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Failed> failed;

    public List<RegisteredMod> getMods() {
        return mods;
    }

    public void setMods(List<RegisteredMod> mods) {
        this.mods = mods;
    }

    public List<Failed> getFailed() {
        return failed;
    }

    public void setFailed(List<Failed> failed) {
        this.failed = failed;
    }

    public void addFailed(Failed failedEntry) {
        if (failed == null) {
            failed = new ArrayList<>();
        }
        failed.add(failedEntry);
    }

    public static class Failed {
        private String filename;
        private String reason;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
