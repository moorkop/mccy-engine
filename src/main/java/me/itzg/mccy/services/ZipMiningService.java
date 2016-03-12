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
    String interrogate(InputStream rawInputStream,
                       Optional<List<ZipMiningHandler.Entry>> handlers) throws IOException;
}
