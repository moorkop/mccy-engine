package me.itzg.mccy.services.assets;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import me.itzg.mccy.model.FmlModRef;
import me.itzg.mccy.model.WorldDescriptor;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.ComparableVersion;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyInvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static me.itzg.mccy.types.MccyConstants.SNAPSHOT_VER_PATTERN;

/**
 * Interprets the <code>level.dat</code> file within World save folders
 * @author Geoff Bourne
 * @since 0.2
 */
@Service
public class LevelDatService {
    private static Logger LOG = LoggerFactory.getLogger(LevelDatService.class);

    private static final String TAG_DATA = "Data";

    private static final String TAG_FML = "FML";
    private static final String TAG_FORGE = "Forge";
    private static final String TAG_FML_MODLIST = "ModList";
    private static final String TAG_FML_MOD_ID = "ModId";
    private static final String TAG_FML_MOD_VERSION = "ModVersion";

    public WorldDescriptor interpret(InputStream levelDatIn) throws IOException, MccyException {

        final NBTInputStream nbtIn = new NBTInputStream(levelDatIn);

        final Tag rootTag = nbtIn.readTag();
        if (rootTag instanceof CompoundTag) {
            final CompoundTag rootCTag = (CompoundTag) rootTag;
            LOG.debug("Loaded root tag: {}", rootCTag);

            final Tag<?> tag = rootCTag.getValue().get(TAG_DATA);
            if (tag instanceof CompoundTag) {
                final CompoundTag dataCTag = (CompoundTag) tag;

                WorldDescriptor worldDescriptor = new WorldDescriptor();

                final CompoundMap dataMap = dataCTag.getValue();

                worldDescriptor.setName(getStringTagValue(dataMap, "LevelName"));

                extractVersionInfo(dataMap, worldDescriptor);

                resolveServerTypeDetails(rootCTag, worldDescriptor);

                return worldDescriptor;

            }
            else {
                throw new MccyInvalidFormatException("Expected Data tag just below root");
            }

        }
        else {
            throw new MccyInvalidFormatException("Expected root tag to be compound");
        }
    }

    private String getStringTagValue(CompoundMap dataMap, String tagName) {
        return ((StringTag) dataMap.get(tagName)).getValue();
    }

    private void resolveServerTypeDetails(CompoundTag rootTag,
                                          WorldDescriptor worldDescriptor) {
        if (worldDescriptor.getServerType() != null) {
            return;
        }

        final CompoundMap rootMap = rootTag.getValue();
        if (rootMap.containsKey(TAG_FML) && rootMap.containsKey(TAG_FORGE)) {
            worldDescriptor.setServerType(ServerType.FORGE);
            final Tag<?> fmlTag = rootMap.get(TAG_FML);
            if (fmlTag instanceof CompoundTag) {
                final CompoundMap fmlMap = (CompoundMap) fmlTag.getValue();
                final Tag<?> modListTag = fmlMap.get(TAG_FML_MODLIST);
                if (modListTag instanceof ListTag) {
                    final List<?> modList = ((ListTag) modListTag).getValue();

                    worldDescriptor.setRequiredMods(
                            modList.stream()
                                    .filter(o -> o instanceof CompoundTag)
                                    .map(o -> {
                                        final CompoundMap modMap = ((CompoundTag) o).getValue();
                                        final FmlModRef modRef = new FmlModRef();
                                        modRef.setId(getStringTagValue(modMap, TAG_FML_MOD_ID));
                                        modRef.setVersion(ComparableVersion.of(getStringTagValue(modMap, TAG_FML_MOD_VERSION)));
                                        return modRef;
                                    })
                                    .collect(Collectors.toList()));
                }
            }
        }
        else {
            worldDescriptor.setServerType(ServerType.VANILLA);

        }
    }

    protected void extractVersionInfo(CompoundMap dataMap, WorldDescriptor worldDescriptor) {
        if (dataMap.containsKey("BorderSize")) {
            // ge 1.8
            if (dataMap.containsKey("Version")) {
                final CompoundTag versionTag = (CompoundTag) dataMap.get("Version");
                final CompoundMap versionData = versionTag.getValue();

                final ByteTag snapshot = (ByteTag) versionData.get("Snapshot");
                final StringTag versionName = (StringTag) versionData.get("Name");
                if (snapshot.getValue().intValue() == 1) {
                    worldDescriptor.setServerType(ServerType.SNAPSHOT);
                    worldDescriptor.setMinecraftVersion(ComparableVersion.of(versionName.getValue(),
                            SNAPSHOT_VER_PATTERN));
                }
                else {
                    worldDescriptor.setMinecraftVersion(ComparableVersion.of(versionName.getValue()));
                }
            }
            else {
                worldDescriptor.setMinecraftVersion(ComparableVersion.of("1.8"));
            }
        }
        else {
            worldDescriptor.setMinecraftVersion(ComparableVersion.of("1.7"));
        }
    }
}
