package me.itzg.mccy.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import me.itzg.mccy.config.MccyFilesSettings;
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
import me.itzg.mccy.repos.ModPackRepo;
import me.itzg.mccy.repos.RegisteredBukkitPluginRepo;
import me.itzg.mccy.repos.RegisteredFmlModRepo;
import me.itzg.mccy.types.Holder;
import me.itzg.mccy.types.MccyConstants;
import me.itzg.mccy.types.MccyException;
import me.itzg.mccy.types.MccyNotFoundException;
import me.itzg.mccy.types.YamlMapper;
import me.itzg.mccy.types.ZipMiningHandler;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.mozilla.universalchardet.UniversalDetector;
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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
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
    private final Pattern PATTERN_UNQUOTED_STR = Pattern.compile("\"(.+?)\"\\s*:\\s*([0-9.\\-_]+)(,?)");
    private final Pattern PATTERN_MISSING_COMMA = Pattern.compile("(\".+?\"\\s*:\\s*\".*?\")\\s*(\".+?\"\\s*:\\s*\".*?\")", Pattern.MULTILINE);

    @Autowired
    private HashFunction fileIdHash;

    @Autowired
    private ZipMiningService zipMiningService;

    @Autowired
    private HashFunction modPackIdHash;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private YamlMapper yamlMapper;

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
    private Pattern filenameGrok = Pattern.compile("([a-zA-Z]+)_?(.*)\\.jar");

    /**
     * Ingests a raw file's input stream, determines first if it's even a valid mod file, and
     * if so stages it for importing.
     * @param fileIn the uploaded file to potentially ingest
     * @return the saved registration object of the ingested mod/plugin
     */
    public RegisteredMod ingest(MultipartFile fileIn) throws MccyException, IOException {

        RegisteredMod registeredMod = traverseZip(fileIn);

        if (registeredMod != null) {
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
            throw new MccyException("Unsupported mod file format. Was looking for items like "+MccyConstants.FILE_MOD_INFO);
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

    private RegisteredMod traverseZip(MultipartFile fileIn) throws IOException, MccyException {
        final Holder<RegisteredMod> registeredModHolder = new Holder<>();

        final String fileHash = zipMiningService.interrogate(fileIn.getInputStream(),
                ZipMiningHandler.listBuilder()
                        .add("META-INF/MANIFEST.MF", (path, in) -> {
                            processManifest(registeredModHolder, in, fileIn.getOriginalFilename());
                        })
                        .add(".*"+MccyConstants.FILE_MOD_INFO, (path, in) -> {
                            processFmlModInfo(registeredModHolder, in);
                        })
                        .add(".*"+MccyConstants.FILE_PLUGIN_META, (path, in) -> {
                            processBukkitPluginMeta(registeredModHolder, in);
                        })
                .build());

        if (registeredModHolder.isSet()) {
            registeredModHolder.get().setId(fileHash);
        }
        return registeredModHolder.get();
    }

    void processBukkitPluginMeta(Holder<RegisteredMod> registeredModHolder, InputStream in) throws IOException {
        final BukkitPluginInfo info = extractBukkitPluginInfo(in);

        final RegisteredBukkitPlugin bukkitPlugin =
                (RegisteredBukkitPlugin) registeredModHolder.getOrCreate(RegisteredBukkitPlugin::new);

        bukkitPlugin.setBukkitPluginInfo(info);
        bukkitPlugin.setVersion(info.getVersion());
        bukkitPlugin.setName(info.getName());
        bukkitPlugin.setDescription(info.getDescription());
        bukkitPlugin.setNativeId(info.getMain());
        bukkitPlugin.setMinecraftVersion(mccyVersionSettings.getDefaultBukkitGameVersion());
    }

    void processFmlModInfo(Holder<RegisteredMod> registeredModHolder, InputStream in) throws IOException {
        final FmlModInfo info = extractFmlModInfo(in);

        if (info.getModList().isEmpty()) {
            throw new IllegalArgumentException("No modList entries were present");
        } else if (info.getModList().size() > 1) {
            throw new IllegalArgumentException("One single-entry modLists are supported at this time");
        }

        RegisteredFmlMod mod = (RegisteredFmlMod) registeredModHolder.getOrCreate(RegisteredFmlMod::new);
        mod.setModInfo(info);

        final FmlModListEntry entry = info.getModList().get(0);
        mod.setName(entry.getName());
        mod.setDescription(entry.getDescription());
        mod.setVersion(entry.getVersion());
        mod.setMinecraftVersion(entry.getMcversion());
        mod.setNativeId(entry.getModid());
        mod.setUrl(entry.getUrl());
    }

    void processManifest(Holder<RegisteredMod> registeredModHolder, InputStream in, String originalFilename) throws IOException {
        final Manifest manifest = new Manifest(in);
        final Attributes mainAttributes = manifest.getMainAttributes();
        final String value = mainAttributes.getValue(MccyConstants.MF_ATTR_FML_CORE_PLUGIN);
        if (value != null) {
            final RegisteredMod registeredMod = registeredModHolder.getOrCreate(RegisteredFmlMod::new);
            if (registeredMod instanceof RegisteredFmlMod) {
                RegisteredFmlMod mod = (RegisteredFmlMod) registeredMod;

                if (mod.getName() == null) {
                    final Matcher matcher = filenameGrok.matcher(originalFilename);
                    if (matcher.matches()) {
                        mod.setName(matcher.group(1));
                        if (mod.getVersion() == null) {
                            mod.setVersion(matcher.group(2));
                        }
                    }
                }
            }
        }
    }

    FmlModInfo extractFmlModInfo(InputStream in) throws IOException {
        final Path tempModInfo = Files.createTempFile("fmlmodinfo", null);
        Files.copy(in, tempModInfo, StandardCopyOption.REPLACE_EXISTING);

        String detectedCharset = UniversalDetector.detectCharset(tempModInfo.toFile());
        if (detectedCharset == null) {
            detectedCharset = filesSettings.getFallbackCharset();
        }
        final Charset cs = Charset.forName(detectedCharset);

        String modinfoJson = fixUnquotedVersionStrings(new String(Files.readAllBytes(tempModInfo), cs));

        modinfoJson = fixMissingCommas(modinfoJson);

        try {
            return objectMapper.readValue(modinfoJson, FmlModInfo.class);
        } catch (JsonParseException | JsonMappingException e) {
            LOG.debug("Failed to parse as full structure, will try list only", e);

            CollectionType modListEntriesType =
                    objectMapper.getTypeFactory()
                            .constructCollectionType(ArrayList.class, FmlModListEntry.class);
            ArrayList<FmlModListEntry> entries =
                    objectMapper.readValue(modinfoJson, modListEntriesType);

            return new FmlModInfo(entries);
        } finally {
            Files.delete(tempModInfo);
        }
    }

    private String fixMissingCommas(String modinfoJson) {
        final Matcher m = PATTERN_MISSING_COMMA.matcher(modinfoJson);

        return m.replaceAll("$1, $2");
    }

    private String fixUnquotedVersionStrings(String s) {
        final Matcher m = PATTERN_UNQUOTED_STR.matcher(s);
        if (m.find()) {
            return m.replaceAll("\"$1\":\"$2\"$3");
        }
        else {
            return s;
        }
    }

    private BukkitPluginInfo extractBukkitPluginInfo(InputStream in) throws IOException {
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

    void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    void setMccyVersionSettings(MccyVersionSettings mccyVersionSettings) {
        this.mccyVersionSettings = mccyVersionSettings;
    }

    void setYamlMapper(YamlMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }
}
