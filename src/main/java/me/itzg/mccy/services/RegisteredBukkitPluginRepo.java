package me.itzg.mccy.services;

import me.itzg.mccy.model.RegisteredBukkitPlugin;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Geoff Bourne
 * @since 1/2/2016
 */
@Repository
public interface RegisteredBukkitPluginRepo extends ElasticsearchCrudRepository<RegisteredBukkitPlugin, String> {
}
