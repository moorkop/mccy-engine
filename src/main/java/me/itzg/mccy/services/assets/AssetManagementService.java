package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface AssetManagementService {
    void saveMetadata(AssetCategory category, String assetId, Asset asset);

    void delete(AssetCategory category, String assetId);
}
