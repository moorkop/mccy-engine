package me.itzg.mccy.types;

import org.springframework.core.io.FileSystemResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A delegating {@link FileSystemResource} that further decorates the response of {@link #getInputStream()}
 * by deleting the wrapped {@link File} on {@link InputStream#close()}.
 *
 * @author Geoff Bourne
 * @since 12/30/2015
 */
public class ReleasingFileSystemResource extends FileSystemResource {
    public ReleasingFileSystemResource(File srcFile) {
        super(srcFile);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new BufferedInputStream(super.getInputStream()) {
            @Override
            public void close() throws IOException {
                super.close();

                ReleasingFileSystemResource.super.getFile().delete();
            }
        };
    }
}
