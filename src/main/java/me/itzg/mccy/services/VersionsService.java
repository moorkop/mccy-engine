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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private MccyVersionSettings mccyVersionSettings;

    private LoadingCache<Boolean, List<ComparableVersion>> officialVersionCache;

    @PostConstruct
    public void init() {
        this.officialVersionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(mccyVersionSettings.getOfficialVersionsCacheTime(), TimeUnit.MINUTES)
                .build(new CacheLoader<Boolean, List<ComparableVersion>>() {
                    @Override
                    public List<ComparableVersion> load(Boolean key) throws Exception {
                        return getOfficialVersions(key);
                    }
                });
    }

    public List<ComparableVersion> getVersions(ServerType type) throws IOException {
        if (type.isOfficial()) {
            return getOfficialVersionsCached(type == ServerType.VANILLA);
        }
        else if (type.isBukkitCompatible()) {
            return Stream.of(mccyVersionSettings.getBukkitVersions())
                    .map(ComparableVersion::new)
                    .collect(Collectors.toList());
        }
        else {
            final ComparableVersion minForgeVersion =
                    new ComparableVersion(mccyVersionSettings.getForgeMinimumVersion());

            return getOfficialVersionsCached(true).stream()
                    .map(v -> squashForgeVersions(v))
                    .filter(v -> v.compareTo(minForgeVersion) >= 0)
                    .distinct()
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toList());
        }

    }

    private ComparableVersion squashForgeVersions(ComparableVersion v) {
        final ComparableVersion[] squashRanges = MccyConstants.FORGE_VERSIONS_SQUASHED;
        for (int i = 0; i+1 < squashRanges.length; i += 2) {
            if (v.ge(squashRanges[i]) && v.lt(squashRanges[i+1])) {
                return v.trim(MccyConstants.FORGE_VERSIONS_SQUASHED_SIZE);
            }
        }
        return v;
    }

    private List<ComparableVersion> getOfficialVersionsCached(boolean isVanilla) throws IOException {
        try {
            return officialVersionCache.get(isVanilla);
        } catch (ExecutionException e) {
            return getOfficialVersions(isVanilla);
        }
    }

    private ComparableVersion squashAbove(ComparableVersion givenVersion, ComparableVersion atOrAboveThisVersion, int squashToTheseParts) {
        if (givenVersion.compareTo(atOrAboveThisVersion) < 0) {
            return givenVersion;
        }
        else {
            return givenVersion.trim(squashToTheseParts);
        }
    }

    private List<ComparableVersion> getOfficialVersions(Boolean isVanilla) throws IOException {
        try (InputStream versionsIn = mccyVersionSettings.getOfficialVersionsUri().toURL().openStream()) {
            final MinecraftVersions content = objectMapper.readValue(versionsIn, MinecraftVersions.class);

            return content.getVersions()
                    .stream()
                    .filter(v -> v.getType() == (isVanilla ?
                            MinecraftVersions.Type.release : MinecraftVersions.Type.snapshot))
                    .map(MinecraftVersions.VersionEntry::getId)
                    .map(ComparableVersion::new)
                    .collect(Collectors.toList());
        }
    }
}
