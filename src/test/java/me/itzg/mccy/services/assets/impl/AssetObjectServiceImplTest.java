package me.itzg.mccy.services.assets.impl;

import me.itzg.mccy.config.MccyAssetSettings;
import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetObject;
import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.repos.AssetObjectRepo;
import me.itzg.mccy.services.assets.AssetObjectService;
import me.itzg.mccy.types.SimpleUUIDGenerator;
import me.itzg.mccy.types.UUIDGenerator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class AssetObjectServiceImplTest {

    private AssetObjectService assetObjectService;
    private AssetObjectRepo assetObjectRepo;
    private UUIDGenerator uuidGenerator;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File storageDir;
    private ElasticsearchTemplate elasticsearchTemplate;

    @Before
    public void setUp() throws Exception {
        assetObjectRepo = Mockito.mock(AssetObjectRepo.class);
        elasticsearchTemplate = Mockito.mock(ElasticsearchTemplate.class);
        uuidGenerator = new SimpleUUIDGenerator();

        AssetObjectServiceImpl assetObjectService = new AssetObjectServiceImpl();
        ReflectionTestUtils.setField(assetObjectService, "assetObjectRepo", assetObjectRepo);
        ReflectionTestUtils.setField(assetObjectService, "uuidGenerator", uuidGenerator);
        ReflectionTestUtils.setField(assetObjectService, "elasticsearchTemplate", elasticsearchTemplate);

        final MccyAssetSettings settings = new MccyAssetSettings();
        storageDir = temporaryFolder.newFolder();
        settings.setStorageDir(storageDir);
        assetObjectService.setSettings(settings);

        this.assetObjectService = assetObjectService;
    }

    @Test
    public void testStore() throws Exception {
        MultipartFile objectFile = new MockMultipartFile("content.zip", "original-content.zip",
                "application/zip", new byte[]{(byte) 0xFE, (byte) 0xED});
        assetObjectService.store(objectFile, "parent-1", AssetObjectPurpose.SOURCE);

        final ArgumentCaptor<AssetObject> savedCaptor = ArgumentCaptor.forClass(AssetObject.class);

        verify(assetObjectRepo).save(savedCaptor.capture());

        final AssetObject saved = savedCaptor.getValue();
        assertThat(saved.getId(), startsWith(SimpleUUIDGenerator.PREFIX));
        assertThat(saved.getAssetId(), equalTo("parent-1"));
        assertThat(saved.getPurpose(), equalTo(AssetObjectPurpose.SOURCE));
        assertThat(saved.getOriginalFileName(), equalTo("original-content.zip"));

        final File[] files = storageDir.listFiles();
        assertThat(files, arrayWithSize(1));
        assertThat(files[0].length(), equalTo(2L));
        final byte[] content = Files.readAllBytes(files[0].toPath());
        assertThat(content, Matchers.equalTo(new byte[]{(byte) 0xFE, (byte) 0xED}));
    }

    @Test
    public void testDeleteObjectsOfAsset() throws Exception {

        final File objFile = new File(storageDir, "file-1.dat");
        assertTrue(objFile.createNewFile());

        when(elasticsearchTemplate.queryForIds(any()))
                .thenReturn(Collections.singletonList("file-1"));

        assetObjectService.deleteObjectsOfAsset("parent-1");

        verify(elasticsearchTemplate).queryForIds(any());
        verify(elasticsearchTemplate).delete(any(DeleteQuery.class), eq(Asset.class));

        assertFalse(objFile.exists());
    }
}