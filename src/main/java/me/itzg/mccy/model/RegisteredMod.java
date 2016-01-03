package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@Document(indexName = "mccy")
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
public abstract class RegisteredMod {

    @Id
    private String id;

    private String name;

    private String description;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String version;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String minecraftVersion;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String nativeId;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String originalFilename;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String url;

    private ServerType[] serverTypes = new ServerType[]{ServerType.FORGE};

    public RegisteredMod() {
    }

    protected RegisteredMod(ServerType[] serverTypes) {
        this.serverTypes = serverTypes;
    }

    /**
     * We'll use an MD5 of the jar file to uniquely fingerprint each one and/or
     * check for redundant uploads.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public void setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }

    public String getNativeId() {
        return nativeId;
    }

    public void setNativeId(String nativeId) {
        this.nativeId = nativeId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ServerType[] getServerTypes() {
        return serverTypes;
    }

    public void setServerTypes(ServerType[] serverTypes) {
        this.serverTypes = serverTypes;
    }
}
