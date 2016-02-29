package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.services.ZipMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
@AssetConsumerSpec(category = AssetCategory.WORLD)
public class WorldAssetsService implements AssetConsumer {

    @Autowired
    private ZipMiningService zipMiningService;

    @Override
    public String consume(MultipartFile assetFile, Authentication auth) {
        return null;
    }
}
