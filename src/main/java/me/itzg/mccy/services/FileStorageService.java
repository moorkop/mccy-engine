package me.itzg.mccy.services;

import me.itzg.mccy.config.MccyFilesSettings;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyNotFoundException;
import me.itzg.mccy.types.ReleasingFileSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@Service
public class FileStorageService {
    private static Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

    private Path repoPath;

    @Autowired
    public void setSettings(MccyFilesSettings settings) throws IOException {
        MccyFilesSettings settings1 = settings;

        repoPath = Paths.get(settings.getRepoDir());
        Files.createDirectories(repoPath);
    }

    public String saveNeedsName(String category, String suffix, boolean temporary, MultipartFile src) throws IOException {
        final Path categoryPath = resolveCategoryPath(category);

        final String originalFilename = src.getOriginalFilename();

        final Path tempFile = Files.createTempFile(categoryPath,
                temporary ? "temp-" : "", suffix);


        // replace the empty placeholder created by createTempFile
        Files.copy(src.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        return tempFile.getFileName().toString();
    }

    public String save(String category, String filename, boolean temporary, MultipartFile src) throws IOException {
        final Path categoryPath = resolveCategoryPath(category);

        final Path outFile = categoryPath.resolve(
                (temporary ? MccyConstants.TEMP_PREFIX : "") + filename
        );

        Files.copy(src.getInputStream(), outFile);

        return outFile.getFileName().toString();
    }

    public void delete(String category, String filename) {
        final Path resolved = repoPath.resolve(category);

        try {
            Files.delete(resolved.resolve(filename));
        } catch (IOException e) {
            LOG.warn("Failed to delete {} file named {}", e, category, filename);
        }
    }

    /**
     *
     * @param category the file storage category
     * @param filename the name of the file within the category
     * @param out where the content of the file will be written. NOTE: this stream will NOT be closed by this
     *            method
     */
    public void copyTo(String category, String filename, OutputStream out) throws IOException, MccyNotFoundException {
        final Path resolved = repoPath.resolve(category);

        try {
            Files.copy(resolved.resolve(filename), out);
        } catch (FileNotFoundException e) {
            // remap
            throw new MccyNotFoundException(e);
        }
    }

    private Path resolveCategoryPath(String category) throws IOException {
        final Path resolved = repoPath.resolve(category);
        Files.createDirectories(resolved);
        return resolved;
    }

    public Resource load(String category, String filename) throws IOException {
        final Path categoryPath = resolveCategoryPath(category);

        final Path filePath = Paths.get(filename);
        if (filePath.isAbsolute()) {
            throw new IllegalArgumentException("Loaded filename cannot be absolute");
        }

        File srcFile = categoryPath.resolve(filePath).toFile();

        return filename.startsWith(MccyConstants.TEMP_PREFIX) ?
                new ReleasingFileSystemResource(srcFile) : new FileSystemResource(srcFile);
    }
}
