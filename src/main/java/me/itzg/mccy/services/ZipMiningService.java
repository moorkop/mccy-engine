package me.itzg.mccy.services;

import me.itzg.mccy.types.ZipMiningHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface ZipMiningService {
    /**
     * Walks a zip file invoking the given handlers and returns a file hash ID.
     * @param rawInputStream the raw input stream of the zip file
     * @param handlers an optional list of handler to be invoked
     * @return the overall file's hash ID
     * @throws IOException
     */
    String interrogate(InputStream rawInputStream,
                       Optional<List<ZipMiningHandler.Entry>> handlers) throws IOException;
}
