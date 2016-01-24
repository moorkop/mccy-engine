package me.itzg.mccy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.itzg.mccy.config.MccyVersionSettings;
import me.itzg.mccy.model.MinecraftVersions;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.ComparableVersion;
import me.itzg.mccy.types.MccyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Encapsulates the lookup of Minecraft versions and the subsets supported by the mod platforms.
 *
 * Created by geoff on 12/27/15.
 */
@Service
public class VersionsService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MccyVersionSettings versionSettings;

    private LoadingCache<Boolean, Collection<String>> officialVersionCache;

    @PostConstruct
    public void init() {
        this.officialVersionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(versionSettings.getOfficialVersionsCacheTime(), TimeUnit.MINUTES)
                .build(new CacheLoader<Boolean, Collection<String>>() {
                    @Override
                    public Collection<String> load(Boolean key) throws Exception {
                        return getOfficialVersions(key);
                    }
                });
    }

    public Collection<String> getVersions(ServerType type) throws IOException {
        if (type.isOfficial()) {
            return getOfficialVersionsCached(type == ServerType.VANILLA);
        }
        else if (type.isBukkitCompatible()) {
            return Arrays.asList(versionSettings.getBukkitVersions());
        }
        else {
            return getOfficialVersionsCached(true).stream()
                    .map(v -> squashAbove(v, MccyConstants.FORGE_VERSION_CUTOFF, 2))
                    .sorted(Collections.reverseOrder())
                    .collect(
                            // use a set to eliminate dupes and treeset to sort
                            Collectors.toCollection(LinkedHashSet::new));
        }

    }

    private Collection<String> getOfficialVersionsCached(boolean isVanilla) throws IOException {
        try {
            return officialVersionCache.get(isVanilla);
        } catch (ExecutionException e) {
            return getOfficialVersions(isVanilla);
        }
    }

    private String squashAbove(String givenVersion, ComparableVersion atOrAboveThisVersion, int squashToTheseParts) {
        ComparableVersion givenComparable = new ComparableVersion(givenVersion);
        if (givenComparable.compareTo(atOrAboveThisVersion) < 0) {
            return givenVersion;
        }
        else {
            return givenComparable.trimToString(squashToTheseParts);
        }
    }

    private List<String> getOfficialVersions(Boolean isVanilla) throws IOException {
        try (InputStream versionsIn = versionSettings.getOfficialVersionsUri().toURL().openStream()) {
            final MinecraftVersions content = objectMapper.readValue(versionsIn, MinecraftVersions.class);

            return content.getVersions()
                    .stream()
                    .filter(v -> v.getType() == (isVanilla ?
                            MinecraftVersions.Type.release : MinecraftVersions.Type.snapshot))
                    .map(MinecraftVersions.VersionEntry::getId)
                    .collect(Collectors.toList());
        }
    }
}
