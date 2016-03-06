package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.Asset;
import me.itzg.mccy.model.AssetObjectPurpose;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.model.WorldDescriptor;
import me.itzg.mccy.repos.AssetRepo;
import me.itzg.mccy.services.ZipMiningService;
import me.itzg.mccy.types.ComparableVersion;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.SimpleUUIDGenerator;
import me.itzg.mccy.types.ZipMiningHandler;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class WorldAssetsServiceTest {

    private WorldAssetsService worldAssetsService;
    private ZipMiningService zipMiningService;
    private LevelDatService levelDatService;
    private AssetRepo assetRepo;
    private AssetObjectService assetObjectService;

    @Before
    public void setUp() throws Exception {
        worldAssetsService = new WorldAssetsService();

        zipMiningService = mock(ZipMiningService.class);
        levelDatService = mock(LevelDatService.class);
        assetRepo = mock(AssetRepo.class);
        assetObjectService = mock(AssetObjectService.class);

        ReflectionTestUtils.setField(worldAssetsService, "zipMiningService", zipMiningService);
        ReflectionTestUtils.setField(worldAssetsService, "levelDatService", levelDatService);
        ReflectionTestUtils.setField(worldAssetsService, "assetRepo", assetRepo);
        ReflectionTestUtils.setField(worldAssetsService, "assetObjectService", assetObjectService);
        ReflectionTestUtils.setField(worldAssetsService, "uuidGenerator", new SimpleUUIDGenerator());

    }

    @Test
    public void testBasicConsumption() throws Exception, MccyException {
        MultipartFile assetFile = new MockMultipartFile("world.zip", new byte[0]);
        Authentication auth = new TestingAuthenticationToken("user1", null);

        when(zipMiningService.interrogate(any(), any()))
                .then(invocationOnMock -> {
                    final Optional<List<ZipMiningHandler.Entry>> handlers = invocationOnMock.getArgumentAt(1, Optional.class);
                    assertNotNull(handlers);
                    final List<ZipMiningHandler.Entry> entries = handlers.orElseThrow(IllegalArgumentException::new);

                    assertThat(entries, not(empty()));

                    InputStream zeroStream = new ByteArrayInputStream(new byte[0]);
                    entries.forEach(entry -> {
                        try {
                            entry.getHandler().handleZipContentFile("/level.dat", zeroStream);
                        } catch (IOException | MccyException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    return "md5-1";
                });

        WorldDescriptor descriptor = new WorldDescriptor();
        descriptor.setName("world-name");
        descriptor.setMinecraftVersion(ComparableVersion.of("1.9"));
        descriptor.setServerType(ServerType.VANILLA);

        when(levelDatService.interpret(any()))
                .thenReturn(descriptor);

        final Asset retAsset = worldAssetsService.consume(assetFile, auth);

        assertNotNull(retAsset);
        assertThat(retAsset.getId(), Matchers.startsWith(SimpleUUIDGenerator.PREFIX));

        final ArgumentCaptor<Asset> assetArgumentCaptor = ArgumentCaptor.forClass(Asset.class);
        verify(assetRepo).save(assetArgumentCaptor.capture());

        final Asset savedAsset = assetArgumentCaptor.getValue();
        assertEquals(retAsset.getId(), savedAsset.getId());
        assertEquals("world-name", savedAsset.getName());
        assertEquals(ComparableVersion.of("1.9"), savedAsset.getCompatibleMcVersion());
        assertEquals(ServerType.VANILLA, savedAsset.getCompatibleMcType());

        verify(assetObjectService).save(same(assetFile), anyString(), eq(AssetObjectPurpose.SOURCE));
    }
}