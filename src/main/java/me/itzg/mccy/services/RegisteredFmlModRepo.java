package me.itzg.mccy.services;

import me.itzg.mccy.model.RegisteredFmlMod;
import me.itzg.mccy.model.RegisteredMod;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@Repository
public interface RegisteredFmlModRepo extends ElasticsearchCrudRepository<RegisteredFmlMod, String> {
    List<RegisteredMod> findByMinecraftVersionOrderByName(String version);
}
