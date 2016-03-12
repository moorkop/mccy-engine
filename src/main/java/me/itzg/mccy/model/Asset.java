package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.itzg.mccy.types.ComparableVersion;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Document(indexName = DocumentCommon.INDEX, type = Asset.TYPE)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
public abstract class Asset<DT> {
    public static final String TYPE = "asset";

    @Id
    @NotNull
    private String id;

    @NotNull
    private AssetCategory category;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String nativeId;
    @NotNull
    private String name;
    private String description;
    private URL homepage;
    private boolean visibleToAll;
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String owner;
    private ComparableVersion version;
    private ComparableVersion compatibleMcVersion;
    private List<ServerType> compatibleMcTypes;
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private List<String> authors;

    @JsonIgnore // so that mapping of 'details' doesn't collide
    public abstract DT getDetails();

    public String getNativeId() {
        return nativeId;
    }

    public void setNativeId(String nativeId) {
        this.nativeId = nativeId;
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

    public URL getHomepage() {
        return homepage;
    }

    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }

    public boolean isVisibleToAll() {
        return visibleToAll;
    }

    public void setVisibleToAll(boolean visibleToAll) {
        this.visibleToAll = visibleToAll;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ComparableVersion getVersion() {
        return version;
    }

    public void setVersion(ComparableVersion version) {
        this.version = version;
    }

    public ComparableVersion getCompatibleMcVersion() {
        return compatibleMcVersion;
    }

    public void setCompatibleMcVersion(ComparableVersion compatibleMcVersion) {
        this.compatibleMcVersion = compatibleMcVersion;
    }

    public List<ServerType> getCompatibleMcTypes() {
        return compatibleMcTypes;
    }

    public void setCompatibleMcTypes(List<ServerType> compatibleMcTypes) {
        this.compatibleMcTypes = compatibleMcTypes;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }
}
