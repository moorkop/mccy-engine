package me.itzg.mccy.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Geoff Bourne
 * @since 1/1/2016
 */
public class RegisteredModReference {
    @NotNull @Size(min = 1)
    private String type;

    @NotNull @Size(min = 1)
    private String id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
