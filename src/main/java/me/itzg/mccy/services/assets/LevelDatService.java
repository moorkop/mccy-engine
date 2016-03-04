package me.itzg.mccy.services.assets;

import me.itzg.mccy.model.WorldDescriptor;
import me.itzg.mccy.types.MccyException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface LevelDatService {
    WorldDescriptor interpret(InputStream levelDatIn) throws IOException, MccyException;
}
