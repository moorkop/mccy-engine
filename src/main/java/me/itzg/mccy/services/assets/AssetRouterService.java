package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.AssetCategory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class AssetRouterService {

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

    public String upload(MultipartFile assetFile, AssetCategory category, Authentication auth) throws IOException {

        final AssetConsumer assetConsumer = consumers.get(category);

        if (assetConsumer != null) {
            return assetConsumer.consume(assetFile, auth);
        }
        else {
            throw new IllegalArgumentException("Asset category is not supported: " + category);
        }
    }
}
