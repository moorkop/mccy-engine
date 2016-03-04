package me.itzg.mccy.repos;

import me.itzg.mccy.model.Asset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Repository
public interface AssetRepo extends CrudRepository<Asset, String> {
}
