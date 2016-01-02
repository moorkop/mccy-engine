package me.itzg.mccy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import me.itzg.mccy.config.MccyFilesSettings;
import me.itzg.mccy.model.RegisteredMod;
import me.itzg.mccy.types.MccyException;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
public class ModsServiceTest {

    @Test
    public void testIngest() throws Exception, MccyException {
        ModsService modsService = new ModsService();

        modsService.setFileIdHash(Hashing.md5());
        modsService.setObjectMapper(new ObjectMapper());
        MccyFilesSettings filesSettings = new MccyFilesSettings();
        modsService.setFilesSettings(filesSettings);

        RegisteredFmlModRepo mockFmlModRepo = mock(RegisteredFmlModRepo.class);
        when(mockFmlModRepo.exists(anyString()))
                .thenReturn(false);
        modsService.setFmlModRepo(mockFmlModRepo);

        FileStorageService fileStorageService = mock(FileStorageService.class);
        when(fileStorageService.save(anyString(), anyString(), anyBoolean(), any()))
                .thenAnswer(invocation -> invocation.getArguments()[1]);
        modsService.setFileStorageService(fileStorageService);

        ClassPathResource resource = new ClassPathResource("test-mod.jar");
        MockMultipartFile multipartFile = new MockMultipartFile("test-mod.jar", resource.getInputStream());

        RegisteredMod registeredMod = modsService.ingest(multipartFile);

        assertNotNull(registeredMod);
        assertEquals("559647387ad6f99432bc4feb5c1b0497", registeredMod.getId());
        assertEquals("Vending", registeredMod.getName());
        assertEquals("1.8", registeredMod.getMinecraftVersion());
        assertEquals("Vending", registeredMod.getNativeId());
    }
}