package me.itzg.mccy.services.impl;

import com.google.common.hash.HashFunction;
import com.google.common.hash.HashingInputStream;
import me.itzg.mccy.services.ZipMiningService;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.ZipMiningHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Mines a given input stream, opens it as a zip stream, and looks for one or more files within the zip
 * calling a handler for each.
 * It also computes the hash of the outer zip file for identification purposes.
 *
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class ZipMiningServiceImpl implements ZipMiningService {
    private static Logger LOG = LoggerFactory.getLogger(ZipMiningService.class);

    @Autowired
    private HashFunction fileIdHash;

    @Override public String interrogate(InputStream rawInputStream,
                              Optional<List<ZipMiningHandler.Entry>> handlers) throws IOException {
        final HashingInputStream hashingInputStream = new HashingInputStream(fileIdHash, rawInputStream);

        final ZipInputStream zipInputStream = new ZipInputStream(hashingInputStream);

        ZipEntry zipEntry;
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (handlers != null) {
                final String name = zipEntry.getName();
                handlers.orElse(Collections.emptyList()).stream()
                        .filter(e -> e.getPath().matcher(name).matches())
                        .forEach(e -> {
                            final ZipMiningHandler handler = e.getHandler();
                            try {
                                handler.handleZipContentFile(name, StreamUtils.nonClosing(zipInputStream));
                            } catch (IOException | MccyException e1) {
                                LOG.warn("Handler {} failed to process zip content file named {}", handler, e1);
                            }
                        });
            }
        }

        // Need to read off the remaining content of the zip file to generate the full hash
        final byte[] buffer = new byte[1024];
        //noinspection StatementWithEmptyBody
        while (hashingInputStream.read(buffer) != -1){}

        return hashingInputStream.hash().toString();
    }
}
