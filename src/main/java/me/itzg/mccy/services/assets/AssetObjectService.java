package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.AssetObjectPurpose;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface AssetObjectService {
    void save(MultipartFile objectFile, String parentAssetId, AssetObjectPurpose purpose);
}
