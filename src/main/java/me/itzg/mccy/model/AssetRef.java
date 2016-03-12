package me.itzg.mccy.model;

/**
 * A minimal reference to an {@link Asset}, typically used when making requests that need to
 * consume an asset.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
public class AssetRef {
    private AssetCategory category;

    private String id;

    public AssetCategory getCategory() {
        return category;
    }

    public AssetRef setCategory(AssetCategory category) {
        this.category = category;
        return this;
    }

    public String getId() {
        return id;
    }

    public AssetRef setId(String id) {
        this.id = id;
        return this;
    }
}
