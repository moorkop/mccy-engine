package me.itzg.mccy.model;

import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.NestedField;

/**
 * @author Geoff Bourne
 * @since 12/29/2015
 */
public class RegisteredFmlMod extends RegisteredMod {

    @NestedField(dotSuffix = "version", type = FieldType.String, index = FieldIndex.not_analyzed)
    private FmlModInfo modInfo;

    public RegisteredFmlMod() {
        super(new ServerType[]{ServerType.FORGE});
    }

    public FmlModInfo getModInfo() {
        return modInfo;
    }

    public void setModInfo(FmlModInfo modInfo) {
        this.modInfo = modInfo;
    }

}
