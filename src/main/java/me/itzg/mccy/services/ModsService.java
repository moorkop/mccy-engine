package me.itzg.mccy.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.HashingInputStream;
import me.itzg.mccy.config.MccyFilesSettings;
import me.itzg.mccy.config.MccySettings;
import me.itzg.mccy.config.MccyVersionSettings;
import me.itzg.mccy.model.BukkitPluginInfo;
import me.itzg.mccy.model.FmlModInfo;
import me.itzg.mccy.model.FmlModListEntry;
import me.itzg.mccy.model.ModPack;
import me.itzg.mccy.model.RegisteredBukkitPlugin;
import me.itzg.mccy.model.RegisteredFmlMod;
import me.itzg.mccy.model.RegisteredMod;
import me.itzg.mccy.model.RegisteredModReference;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyNotFoundException;
import me.itzg.mccy.types.YamlMapper;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@Service
public class ModsService {
    private static Logger LOG = LoggerFactory.getLogger(ModsService.class);

    @Autowired
    private HashFunction fileIdHash;

    @Autowired
    private HashFunction modPackIdHash;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private YamlMapper yamlMapper;

    @Autowired
    private MccySettings mccySettings;

    @Autowired
    private MccyVersionSettings mccyVersionSettings;

    @Autowired
    private MccyFilesSettings filesSettings;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private RegisteredFmlModRepo fmlModRepo;

    @Autowired
    private RegisteredBukkitPluginRepo bukkitPluginRepo;

    @Autowired
    private ModPackRepo modPackRepo;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * Ingests a raw file's input stream, determines first if it's even a valid mod file, and
     * if so stages it for importing.
     * @param fileIn the uploaded file to potentially ingest
     * @return the saved registration object of the ingested mod/plugin
     */
    public RegisteredMod ingest(MultipartFile fileIn) throws MccyException, IOException {

        final HashingInputStream hashingIn = new HashingInputStream(fileIdHash, fileIn.getInputStream());

        final ZipInputStream zipIn = new ZipInputStream(hashingIn);

        RegisteredMod registeredMod = traverseZip(zipIn);

        zipIn.close();

        if (registeredMod != null) {
            registeredMod.setId(hashingIn.hash().toString());
            registeredMod.setOriginalFilename(fileIn.getOriginalFilename());

            final String id = registeredMod.getId();

            if (registeredMod instanceof RegisteredFmlMod) {
                final RegisteredFmlMod existing = fmlModRepo.findOne(id);
                if (existing != null) {
                    return existing;
                }
                fmlModRepo.save((RegisteredFmlMod) registeredMod);

            } else if (registeredMod instanceof RegisteredBukkitPlugin) {
                final RegisteredBukkitPlugin existing = bukkitPluginRepo.findOne(id);
                if (existing != null) {
                    return existing;
                }
                bukkitPluginRepo.save((RegisteredBukkitPlugin) registeredMod);

            }

            fileStorageService.save(MccyConstants.CATEGORY_MODS, id + MccyConstants.EXT_MODS, false, fileIn);

            return registeredMod;
        }
        else {
            throw new MccyException("Unsupported mod file format. Was looking for items like "+MccyConstants.FILE_MCMOD_INFO);
        }

    }

    public void save(String id, RegisteredMod mod) {
        LOG.debug("Saving {} : {}", id, mod);

        if (mod instanceof RegisteredFmlMod) {
            fmlModRepo.save((RegisteredFmlMod) mod);
        } else if (mod instanceof RegisteredBukkitPlugin) {
            bukkitPluginRepo.save((RegisteredBukkitPlugin) mod);
        }
    }

    private RegisteredMod traverseZip(ZipInputStream zipIn) throws IOException, MccyException {
        RegisteredMod registeredMod = null;

        try {
            ZipEntry nextEntry;
            while ((nextEntry = zipIn.getNextEntry()) != null) {
                final String entryName = nextEntry.getName();

                if (entryName.endsWith(MccyConstants.FILE_MCMOD_INFO)) {
                    registeredMod = from(extractFmlModInfo(zipIn));
                } else if (entryName.endsWith(MccyConstants.FILE_PLUGIN_META)) {
                    registeredMod = from(extractBukkitPluginInfo(zipIn));
                }
            }

        } catch (ZipException e) {
            throw new MccyException("Given mod file is not a valid zip file", e);
        }
        return registeredMod;
    }

    FmlModInfo extractFmlModInfo(InputStream in) throws IOException {

        // We need to wrap it and mark it since
        // a) not all input streams (i.e. ZipInputStream) are mark-able
        // b) we'll need to rewind if the initial JSON parse fails
        BufferedInputStream bufferedIn = new BufferedInputStream(in);
        bufferedIn.mark(filesSettings.getMcInfoReadLimit());

        try {
            return objectMapper.readValue(StreamUtils.nonClosing(bufferedIn), FmlModInfo.class);
        } catch (JsonParseException | JsonMappingException e) {
            LOG.debug("Failed to parse as full structure, will try list only", e);

            bufferedIn.reset();
            CollectionType modListEntriesType =
                    objectMapper.getTypeFactory()
                            .constructCollectionType(ArrayList.class, FmlModListEntry.class);
            ArrayList<FmlModListEntry> entries =
                    objectMapper.readValue(StreamUtils.nonClosing(bufferedIn), modListEntriesType);

            return new FmlModInfo(entries);
        }
    }

    BukkitPluginInfo extractBukkitPluginInfo(InputStream in) throws IOException {
        return yamlMapper.getMapper().readValue(StreamUtils.nonClosing(in), BukkitPluginInfo.class);
    }

    /**
     * Takes the given mod pack ID and generates zip content containing the mods of that pack
     * @param modPackId the ID of the {@link ModPack}
     * @param outputStream where the resulting zip should be written
     * @throws MccyNotFoundException if the mod pack didn't exist
     * @throws IOException
     */
    public void buildModPack(String modPackId, ServletOutputStream outputStream) throws MccyNotFoundException, IOException {
        final ModPack modPack = modPackRepo.findOne(modPackId);

        if (modPack == null) {
            throw new MccyNotFoundException("The mod pack does not exist: "+modPackId);
        }

        final ZipOutputStream zipOut = new ZipOutputStream(outputStream);

        for (String modId : modPack.getMods()) {
            final String filename = modId + MccyConstants.EXT_MODS;
            zipOut.putNextEntry(new ZipEntry(getOriginalFileName(modId)));

            fileStorageService.copyTo(MccyConstants.CATEGORY_MODS, filename, zipOut);

            zipOut.closeEntry();
        }

        zipOut.close();
    }

    private String getOriginalFileName(String modId) throws MccyNotFoundException {
        final RegisteredFmlMod fmlMod = fmlModRepo.findOne(modId);
        if (fmlMod != null) {
            return fmlMod.getOriginalFilename();
        }

        final RegisteredBukkitPlugin bukkitPlugin = bukkitPluginRepo.findOne(modId);
        if (bukkitPlugin != null) {
            return bukkitPlugin.getOriginalFilename();
        }

        throw new MccyNotFoundException("Unable to locate an FML mod or Bukkit plugin with id " + modId);
    }

    RegisteredMod from(FmlModInfo info) {
        if (info.getModList().isEmpty()) {
            throw new IllegalArgumentException("No modList entries were present");
        } else if (info.getModList().size() > 1) {
            throw new IllegalArgumentException("One single-entry modLists are supported at this time");
        }

        RegisteredFmlMod mod = new RegisteredFmlMod();
        mod.setModInfo(info);

        final FmlModListEntry entry = info.getModList().get(0);
        mod.setName(entry.getName());
        mod.setDescription(entry.getDescription());
        mod.setVersion(entry.getVersion());
        mod.setMinecraftVersion(entry.getMcversion());
        mod.setNativeId(entry.getModid());
        mod.setUrl(entry.getUrl());

        return mod;
    }

    RegisteredMod from(BukkitPluginInfo info) {
        final RegisteredBukkitPlugin bukkitPlugin = new RegisteredBukkitPlugin();

        bukkitPlugin.setVersion(info.getVersion());
        bukkitPlugin.setName(info.getName());
        bukkitPlugin.setDescription(info.getDescription());
        bukkitPlugin.setNativeId(info.getMain());
        bukkitPlugin.setMinecraftVersion(mccyVersionSettings.getDefaultBukkitGameVersion());

        return bukkitPlugin;
    }

    public List<? extends RegisteredMod> queryAll() {
        final List<RegisteredMod> all = new ArrayList<>();

        for (RegisteredFmlMod mod : fmlModRepo.findAll(new Sort("name"))) {
            all.add(mod);
        }
        for (RegisteredBukkitPlugin plugin : bukkitPluginRepo.findAll(new Sort("name"))) {
            all.add(plugin);
        }

        return all;
    }

    public List<? extends RegisteredMod> queryByMinecraftVersion(String mcversion) {
        return fmlModRepo.findByMinecraftVersionOrderByName(mcversion);
    }

    public List<? extends RegisteredMod> querySuggestions(String mcversion, ServerType serverType, String input) {

        final NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        final QueryBuilder query;
        if (!Strings.isNullOrEmpty(input)) {
            query = QueryBuilders.boolQuery()
                    .minimumNumberShouldMatch(1)
                    .should(prefixQuery("name", input).boost(2.0f))
                    .should(prefixQuery("description", input));
        }
        else {
            query = QueryBuilders.matchAllQuery();
        }

        final NativeSearchQuery searchQuery = queryBuilder
                .withQuery(query)
                .withFilter(termFilter("minecraftVersion", mcversion))
                .build();

        if (serverType.isBukkitCompatible()) {
            return esTemplate.queryForList(searchQuery, RegisteredBukkitPlugin.class);
        }
        else {
            return esTemplate.queryForList(searchQuery, RegisteredFmlMod.class);
        }
    }

    public void delete(String id) {
        fmlModRepo.delete(id);
        fileStorageService.delete(MccyConstants.CATEGORY_MODS, id+MccyConstants.EXT_MODS);
    }

    public String registerModPack(Collection<RegisteredModReference> registeredModRefs) {
        String type = null;
        List<String> registeredModIds = new ArrayList<>(registeredModRefs.size());
        for (RegisteredModReference modRef : registeredModRefs) {
            if (type == null) {
                type = modRef.getType();
            } else if (!type.equals(modRef.getType())) {
                throw new IllegalArgumentException("The list included an improper mix of mod types");
            }
            registeredModIds.add(modRef.getId());
        }

        final String modPackId = ModPack.computeId(modPackIdHash, registeredModIds);

        if (!allModsExist(registeredModIds)) {
            throw new IllegalArgumentException("One or more mods do not exist");
        }

        // TODO make this transactional...if it's worth it

        ModPack modPack = modPackRepo.findOne(modPackId);
        if (modPack != null) {
            touchModPack(modPack);
            return modPackId;
        }
        else {
            modPack = new ModPack();
            modPack.setId(modPackId);
            modPack.setLastAccess(new Date());
            modPack.setModType(type);
            modPack.setMods(registeredModIds);
            modPackRepo.save(modPack);
        }

        return modPackId;
    }

    private boolean allModsExist(Collection<String> registeredModIds) {
        return registeredModIds.stream()
                .allMatch(id -> fmlModRepo.exists(id) || bukkitPluginRepo.exists(id));
    }

    private void touchModPack(ModPack modPack) {
        // TODO make this transactional...if it's worth it
        modPack.setLastAccess(new Date());
        modPackRepo.save(modPack);
    }

    void setFileIdHash(HashFunction fileIdHash) {
        this.fileIdHash = fileIdHash;
    }

    void setFilesSettings(MccyFilesSettings filesSettings) {
        this.filesSettings = filesSettings;
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    void setFmlModRepo(RegisteredFmlModRepo fmlModRepo) {
        this.fmlModRepo = fmlModRepo;
    }

    void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    void setMccySettings(MccySettings mccySettings) {
        this.mccySettings = mccySettings;
    }

    void setYamlMapper(YamlMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }
}
