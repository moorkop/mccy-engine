package me.itzg.mccy.services;

import me.itzg.mccy.types.MccyConstants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 1/6/2016
 */
public class FileStorageServiceTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testSaveNeedsName() throws Exception {

        final FileStorageService service = new FileStorageService();
        final Path repoPath = temp.newFolder().toPath();
        org.springframework.test.util.ReflectionTestUtils.setField(service, "repoPath", repoPath);

        MultipartFile src;
        try (InputStream in = new ClassPathResource("SkyGrid.zip").getInputStream()) {
            src = new MockMultipartFile("SkyGrid.zip", in);
        }

        final String filename = service.saveNeedsName(MccyConstants.CATEGORY_WORLDS, ".zip", true, src);

        assertNotNull(filename);
        final Path pathToFile = repoPath.resolve(MccyConstants.CATEGORY_WORLDS).resolve(filename);
        assertTrue(Files.exists(pathToFile));
        assertEquals(3946L, Files.size(pathToFile));
    }
}