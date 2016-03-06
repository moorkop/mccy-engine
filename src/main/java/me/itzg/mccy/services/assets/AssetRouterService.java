package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.types.MccyInvalidFormatException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class AssetRouterService {

    @Autowired
    private AssetObjectService assetObjectService;

    @Autowired
    private AssetRepo assetRepo;

    private Map<AssetCategory, AssetConsumer> consumers;

    @Autowired
    public void setBeanFactory(ListableBeanFactory beanFactory) {
        final Map<String, Object> beans = beanFactory.getBeansWithAnnotation(AssetConsumerSpec.class);

        consumers = new EnumMap<>(AssetCategory.class);

        beans.values().forEach(b -> {
            final AssetConsumerSpec info = b.getClass().getAnnotation(AssetConsumerSpec.class);

            consumers.put(info.category(), ((AssetConsumer) b));
        });
    }

    public String upload(MultipartFile assetFile, AssetCategory category, Authentication auth) throws IOException, MccyInvalidFormatException {

        final AssetConsumer assetConsumer = consumers.get(category);

        if (assetConsumer != null) {
            return assetConsumer.consume(assetFile, auth);
        }
        else {
            throw new IllegalArgumentException("Asset category is not supported: " + category);
        }
    }

    public Resource downloadObject(AssetCategory category,
                                   AssetObjectPurpose assetObjectPurpose, String assetId) throws FileNotFoundException {

        return assetObjectService.retrieve(assetId, assetObjectPurpose);
    }

    public List<Asset> queryByCategory(AssetCategory category) {
        return assetRepo.findByCategory(category);
    }
}
