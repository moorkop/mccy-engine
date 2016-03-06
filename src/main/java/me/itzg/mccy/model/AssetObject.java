package me.itzg.mccy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Parent;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Document(indexName = DocumentCommon.INDEX, type = "asset_object")
public class AssetObject {

    @Id
    private String id;

    @Parent(type = Asset.TYPE)
    private String assetId;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String originalFileName;

    private AssetObjectPurpose purpose;

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public AssetObjectPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(AssetObjectPurpose purpose) {
        this.purpose = purpose;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
