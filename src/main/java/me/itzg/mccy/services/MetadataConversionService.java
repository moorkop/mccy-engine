package me.itzg.mccy.services;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public interface MetadataConversionService {
    void fillFromEnv(List<String> env, Object target);
}
