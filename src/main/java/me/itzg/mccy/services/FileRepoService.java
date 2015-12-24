package me.itzg.mccy.services;

import com.google.common.base.Strings;
import me.itzg.mccy.config.MccyFilesSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Geoff Bourne
 * @since 12/23/2015
 */
@Service
public class FileRepoService {

    private MccyFilesSettings settings;
    private Path repoPath;

    @Autowired
    public void setSettings(MccyFilesSettings settings) throws IOException {
        this.settings = settings;

        repoPath = Paths.get(settings.getRepoDir());
        Files.createDirectories(repoPath);
    }

    public String save(String category, String suffix, MultipartFile src) throws IOException {
        final Path categoryPath = resolveCategoryPath(category);

        final String originalFilename = src.getOriginalFilename();

        final Path tempFile;
        if (!Strings.isNullOrEmpty(originalFilename) && originalFilename.endsWith(suffix)) {
            tempFile = categoryPath.resolve(originalFilename);
        }
        else {
            tempFile = Files.createTempFile(categoryPath, "", suffix);
        }

        src.transferTo(tempFile.toAbsolutePath().toFile());

        return tempFile.getFileName().toString();
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

        return new FileSystemResource(categoryPath.resolve(filePath).toFile());
    }
}
