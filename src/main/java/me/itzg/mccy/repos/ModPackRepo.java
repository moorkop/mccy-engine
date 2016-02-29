package me.itzg.mccy.repos;

import me.itzg.mccy.model.ModPack;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Geoff Bourne
 * @since 1/1/2016
 */
@Repository
public interface ModPackRepo extends ElasticsearchCrudRepository<ModPack, String> {
}
