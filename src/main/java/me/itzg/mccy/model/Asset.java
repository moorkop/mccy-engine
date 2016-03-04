package me.itzg.mccy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.itzg.mccy.types.ComparableVersion;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

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
    private String id;

    @Field(index = FieldIndex.not_analyzed)
    private String nativeId;
    private String name;
    private String description;
    private URL homepage;
    private boolean visibleToPublic;
    @Field(index = FieldIndex.not_analyzed)
    private String owner;
    private ComparableVersion version;
    private ComparableVersion compatibleMcVersion;
    private ServerType compatibleMcType;
    @Field(index = FieldIndex.not_analyzed)
    private List<String> authors;

    public abstract AssetCategory getCategory();

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

    public boolean isVisibleToPublic() {
        return visibleToPublic;
    }

    public void setVisibleToPublic(boolean visibleToPublic) {
        this.visibleToPublic = visibleToPublic;
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

    public ServerType getCompatibleMcType() {
        return compatibleMcType;
    }

    public void setCompatibleMcType(ServerType compatibleMcType) {
        this.compatibleMcType = compatibleMcType;
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
}
