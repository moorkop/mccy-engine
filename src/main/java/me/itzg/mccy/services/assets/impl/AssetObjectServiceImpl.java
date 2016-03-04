package me.itzg.mccy.services.assets.impl;

import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.services.assets.AssetObjectService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class AssetObjectServiceImpl implements AssetObjectService {
    @Override public void save(MultipartFile objectFile, String parentAssetId, AssetObjectPurpose purpose) {

    }
}
