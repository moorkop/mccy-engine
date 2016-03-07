package me.itzg.mccy.services.assets.impl;

import com.google.common.base.Strings;
import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.services.assets.AssetManagementService;
import me.itzg.mccy.services.assets.AssetObjectService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class AssetManagementServiceImpl implements AssetManagementService {

    @Autowired
    private AssetRepo assetRepo;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

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

    @Override
    public List<Asset> suggest(AssetCategory category, String input) {
        final QueryBuilder query;
        if (!Strings.isNullOrEmpty(input)) {
            query = QueryBuilders.boolQuery()
                    .must(matchQuery("category", category))
                    .minimumNumberShouldMatch(1)
                    .should(prefixQuery("name", input).boost(2.0f))
                    .should(prefixQuery("description", input));
        }
        else {
            query = QueryBuilders.matchAllQuery();
        }

        return elasticsearchTemplate.queryForList(new NativeSearchQuery(query), Asset.class);
    }
}
