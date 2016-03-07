package me.itzg.mccy.services.assets.impl;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.services.assets.AssetManagementService;
import me.itzg.mccy.services.assets.AssetObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class AssetManagementServiceImpl implements AssetManagementService {

    @Autowired
    private AssetRepo assetRepo;

    @Autowired
    private AssetObjectService assetObjectService;

    @Override
    public void saveMetadata(AssetCategory category, String assetId, Asset asset) {
        assetRepo.save(asset);
    }

    @Override
    public void delete(AssetCategory category, String assetId) {
        assetObjectService.deleteObjectsOfAsset(assetId);

        assetRepo.delete(assetId);
    }
}
