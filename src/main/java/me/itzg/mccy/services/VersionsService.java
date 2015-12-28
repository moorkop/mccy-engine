package me.itzg.mccy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.model.MinecraftVersions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by geoff on 12/27/15.
 */
@Service
public class VersionsService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MccySettings settings;

    public List<String> getVersions(MinecraftVersions.Type type) throws IOException {
        try (InputStream versionsIn = settings.getOfficialVersionsUri().toURL().openStream()) {
            final MinecraftVersions content = objectMapper.readValue(versionsIn, MinecraftVersions.class);

            return content.getVersions()
                    .stream()
                    .filter(v -> v.getType() == type)
                    .map(v -> v.getId())
                    .collect(Collectors.toList());
        }

    }
}
