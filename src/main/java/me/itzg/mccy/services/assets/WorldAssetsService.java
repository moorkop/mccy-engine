package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.model.WorldAsset;
import me.itzg.mccy.model.WorldDescriptor;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.services.ZipMiningService;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyInvalidFormatException;
import me.itzg.mccy.types.UUIDGenerator;
import me.itzg.mccy.types.ZipMiningHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
@AssetConsumerSpec(category = AssetCategory.WORLD)
public class WorldAssetsService implements AssetConsumer {

    @Autowired
    private ZipMiningService zipMiningService;

    @Autowired
    private LevelDatService levelDatService;

    @Autowired
    private AssetRepo assetRepo;

    @Autowired
    private AssetObjectService assetObjectService;

    @Autowired
    private UUIDGenerator uuidGenerator;

    @Override
    public Asset consume(MultipartFile assetFile, Authentication auth) throws IOException, MccyInvalidFormatException {
        final Consumption consumption = new Consumption();

        zipMiningService.interrogate(assetFile.getInputStream(), ZipMiningHandler.listBuilder()
                .add(".*/level.dat", consumption::consumeLevelDat)
                .build());

        if (consumption.worldDescriptor != null) {
            final WorldAsset asset = new WorldAsset();
            asset.setCategory(AssetCategory.WORLD);
            final String id = uuidGenerator.generate().toString();
            asset.setId(id);

            fillFromDescriptor(asset, consumption.worldDescriptor);

            assetRepo.save(asset);

            assetObjectService.save(assetFile, id, AssetObjectPurpose.SOURCE);

            return asset;
        }

        throw new MccyInvalidFormatException("The given file was not a world save");
    }

    private void fillFromDescriptor(WorldAsset asset, WorldDescriptor worldDescriptor) {
        asset.setWorldDetails(worldDescriptor);
        asset.setName(worldDescriptor.getName());
        asset.setCompatibleMcVersion(worldDescriptor.getMinecraftVersion());
        asset.setCompatibleMcType(worldDescriptor.getServerType());
    }


    private class Consumption {

        private WorldDescriptor worldDescriptor;

        public void consumeLevelDat(String path, InputStream in) throws IOException, MccyException {
            worldDescriptor = levelDatService.interpret(in);
        }
    }
}
