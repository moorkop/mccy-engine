package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.AssetObjectPurpose;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface AssetObjectService {
    void store(MultipartFile objectFile, String parentAssetId, AssetObjectPurpose purpose) throws IOException;

    Resource retrieve(String assetId) throws FileNotFoundException;

    void deleteObjectsOfAsset(String assetId);
}
