package me.itzg.mccy.services.assets.impl;

import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetObject;
import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.repos.AssetObjectRepo;
import me.itzg.mccy.services.assets.AssetObjectService;
import me.itzg.mccy.types.UUIDGenerator;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasParentQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class AssetObjectServiceImpl implements AssetObjectService {

    private static final String SUFFIX = ".dat";

    @Autowired
    private AssetObjectRepo assetObjectRepo;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private UUIDGenerator uuidGenerator;

    private Path storagePath;

    @Override public void save(MultipartFile objectFile, String parentAssetId, AssetObjectPurpose purpose) throws IOException {
        final String id = uuidGenerator.generate().toString();

        final Path filePath = storagePath.resolve(id + SUFFIX);
        Files.copy(objectFile.getInputStream(), filePath);

        try {
            final AssetObject assetObject = new AssetObject();
            assetObject.setId(id);
            assetObject.setAssetId(parentAssetId);
            assetObject.setOriginalFileName(objectFile.getOriginalFilename());
            assetObject.setPurpose(purpose);
            assetObjectRepo.save(assetObject);
        } catch (Exception e) {
            Files.delete(filePath);
            throw e;
        }
    }

    @Override
    public Resource retrieve(String assetId, AssetObjectPurpose assetObjectPurpose) throws FileNotFoundException {

        final HasParentQueryBuilder hasParentQuery =
                QueryBuilders.hasParentQuery(Asset.TYPE, QueryBuilders.matchQuery("id", assetId));

        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(hasParentQuery)
                .must(QueryBuilders.matchQuery("purpose", assetObjectPurpose));

        final List<AssetObject> results =
                elasticsearchTemplate.queryForList(new NativeSearchQuery(queryBuilder), AssetObject.class);

        final AssetObject assetObject = results.stream()
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("Asset object does not exist"));

        final Path assetObjPath = storagePath.resolve(assetObject.getId() + SUFFIX);
        return new FileSystemResource(assetObjPath.toFile());
    }

    @Autowired
    public void setSettings(MccyAssetSettings settings) throws IOException {
        storagePath = settings.getStorageDir().toPath();
        Files.createDirectories(storagePath);
    }
}
