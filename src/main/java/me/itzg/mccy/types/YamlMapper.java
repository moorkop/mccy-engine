package me.itzg.mccy.types;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serves to avoid auto-configuration and use of the standard JSON based ObjectMapper.
 *
 * @author Geoff Bourne
 * @since 1/2/2016
 */
public class YamlMapper {
    private final ObjectMapper mapper;

    public YamlMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
