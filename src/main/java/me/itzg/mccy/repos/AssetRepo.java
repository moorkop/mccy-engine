package me.itzg.mccy.repos;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
@Repository
public interface AssetRepo extends CrudRepository<Asset, String> {
    List<Asset> findByCategory(AssetCategory category);
}
