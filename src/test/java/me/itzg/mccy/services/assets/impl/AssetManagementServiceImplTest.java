package me.itzg.mccy.services.assets.impl;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetCategory;
import me.itzg.mccy.model.WorldAsset;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.services.assets.AssetManagementService;
import me.itzg.mccy.services.assets.AssetObjectService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class AssetManagementServiceImplTest {
    private AssetManagementService assetManagementService;
    private AssetRepo assetRepo;
    private AssetObjectService assetObjectService;

    @Before
    public void setUp() throws Exception {
        final AssetManagementServiceImpl assetManagementService = new AssetManagementServiceImpl();
        assetRepo = mock(AssetRepo.class);
        assetObjectService = mock(AssetObjectService.class);

        ReflectionTestUtils.setField(assetManagementService, "assetRepo", assetRepo);
        ReflectionTestUtils.setField(assetManagementService, "assetObjectService", assetObjectService);

        this.assetManagementService = assetManagementService;
    }

    @Test
    public void testSaveMetadata() throws Exception {

        Asset asset = new WorldAsset();
        asset.setId("asset-1");
        asset.setDescription("new description");
        assetManagementService.saveMetadata(AssetCategory.WORLD, "asset-1", asset);

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetArgumentCaptor.capture());

        final Asset actualAsset = assetArgumentCaptor.getValue();
        assertEquals("new description", actualAsset.getDescription());
    }

    @Test
    public void testDelete() throws Exception {
        assetManagementService.delete(AssetCategory.WORLD, "asset-1");

        verify(assetObjectService).deleteObjectsOfAsset("asset-1");
        verify(assetRepo).delete("asset-1");
    }
}