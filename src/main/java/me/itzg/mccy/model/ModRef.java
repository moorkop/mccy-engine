package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.itzg.mccy.types.ComparableVersion;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
public class ModRef {
    private String id;
    private ComparableVersion version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ComparableVersion getVersion() {
        return version;
    }

    public void setVersion(ComparableVersion version) {
        this.version = version;
    }
}
