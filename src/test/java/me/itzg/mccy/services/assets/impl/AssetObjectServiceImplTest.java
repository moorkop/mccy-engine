package me.itzg.mccy.services.assets.impl;

import me.itzg.mccy.config.MccyAssetSettings;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

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

    @Before
    public void setUp() throws Exception {
        assetObjectRepo = Mockito.mock(AssetObjectRepo.class);
        uuidGenerator = new SimpleUUIDGenerator();

        AssetObjectServiceImpl assetObjectService = new AssetObjectServiceImpl();
        ReflectionTestUtils.setField(assetObjectService, "assetObjectRepo", assetObjectRepo);
        ReflectionTestUtils.setField(assetObjectService, "uuidGenerator", uuidGenerator);

        final MccyAssetSettings settings = new MccyAssetSettings();
        storageDir = temporaryFolder.newFolder();
        settings.setStorageDir(storageDir);
        assetObjectService.setSettings(settings);

        this.assetObjectService = assetObjectService;
    }

    @Test
    public void testSave() throws Exception {
        MultipartFile objectFile = new MockMultipartFile("content.zip", "original-content.zip",
                "application/zip", new byte[]{(byte) 0xFE, (byte) 0xED});
        assetObjectService.save(objectFile, "parent-1", AssetObjectPurpose.SOURCE);

        final ArgumentCaptor<AssetObject> savedCaptor = ArgumentCaptor.forClass(AssetObject.class);

        Mockito.verify(assetObjectRepo).save(savedCaptor.capture());

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

}