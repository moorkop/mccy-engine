package me.itzg.mccy.repos;

import me.itzg.mccy.model.AssetObject;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Repository
public interface AssetObjectRepo extends ElasticsearchCrudRepository<AssetObject, String> {

}
