package me.itzg.mccy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import me.itzg.mccy.config.GeneralConfig;
import me.itzg.mccy.config.MccyFilesSettings;
import me.itzg.mccy.config.MccyVersionSettings;
import me.itzg.mccy.model.BukkitPluginInfo;
import me.itzg.mccy.model.FmlModInfo;
import me.itzg.mccy.model.RegisteredBukkitPlugin;
import me.itzg.mccy.model.RegisteredMod;
import me.itzg.mccy.repos.ModPackRepo;
import me.itzg.mccy.repos.RegisteredBukkitPluginRepo;
import me.itzg.mccy.repos.RegisteredFmlModRepo;
import me.itzg.mccy.services.impl.ZipMiningServiceImpl;
import me.itzg.mccy.types.Holder;
import me.itzg.mccy.types.MccyException;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 12/30/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        JacksonAutoConfiguration.class,
        ModsServiceTest.TestConfig.class,
        GeneralConfig.class
},
        initializers = ConfigFileApplicationContextInitializer.class
)
public class ModsServiceTest {

    @Configuration
    public static class TestConfig {
        @Bean
        public ModsService modsService() {
            return new ModsService();
        }

        @Bean
        public HashFunction fileIdHash() {
            return Hashing.md5();
        }

        @Bean
        public MccyVersionSettings mccyVersionSettings() {
            return new MccyVersionSettings();
        }

        @Bean
        public MccyFilesSettings mccyFilesSettings() {
            return new MccyFilesSettings();
        }

        @Bean @Qualifier("mock")
        public FileStorageService fileStorageService() {
            return Mockito.mock(FileStorageService.class);
        }

        @Bean @Qualifier("mock")
        public RegisteredFmlModRepo registeredFmlModRepo() {
            return Mockito.mock(RegisteredFmlModRepo.class);
        }

        @Bean @Qualifier("mock")
        public RegisteredBukkitPluginRepo registeredBukkitPluginRepo() {
            return Mockito.mock(RegisteredBukkitPluginRepo.class);
        }

        @Bean @Qualifier("mock")
        public ModPackRepo modPackRepo() {
            return Mockito.mock(ModPackRepo.class);
        }

        @Bean @Qualifier("mock")
        public ElasticsearchTemplate elasticsearchTemplate() {
            return Mockito.mock(ElasticsearchTemplate.class);
        }

        @Bean
        public ZipMiningService zipMiningService() {
            return new ZipMiningServiceImpl();
        }

    }

    @Autowired
    private ModsService modsService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testIngest() throws Exception, MccyException {

        ClassPathResource resource = new ClassPathResource("fml/test-mod.jar");
        MockMultipartFile multipartFile = new MockMultipartFile("test-mod.jar", resource.getInputStream());

        RegisteredMod registeredMod = modsService.ingest(multipartFile);

        assertNotNull(registeredMod);
        assertEquals("559647387ad6f99432bc4feb5c1b0497", registeredMod.getId());
        assertEquals("Vending", registeredMod.getName());
        assertEquals("1.8", registeredMod.getMinecraftVersion());
        assertEquals("Vending", registeredMod.getNativeId());
    }

    @Test
    public void testBukkitPluginInfoParsing() throws Exception {
        final ClassPathResource resource = new ClassPathResource("plugin.yml");

        Holder<RegisteredMod> holder = new Holder<>();
        modsService.processBukkitPluginMeta(
                holder, resource.getInputStream()
        );

        assertTrue(holder.isInstanceOf(RegisteredBukkitPlugin.class));

        final RegisteredMod registeredMod = holder.get();
        assertNotNull(registeredMod);
        assertEquals("1.8", registeredMod.getMinecraftVersion());
        assertEquals("ShowCaseStandalone", registeredMod.getName());
        assertEquals("ShowCaseStandalone allows you to create mini shops on top of blocks.  The item for\n" +
                "sale or purchase is displayed on top of the block.\n", registeredMod.getDescription());
        assertEquals("730", registeredMod.getVersion());

        final BukkitPluginInfo info = ((RegisteredBukkitPlugin) holder.get()).getBukkitPluginInfo();

        assertNotNull(info);
        assertEquals("ShowCaseStandalone", info.getName());
        assertEquals("730", info.getVersion());
        assertEquals("ShowCaseStandalone allows you to create mini shops on top of blocks.  The item for\n" +
                "sale or purchase is displayed on top of the block.\n", info.getDescription());

    }

    @Test
    public void testUnquotedVersionStrInMcmodInfo() throws Exception {
        final ClassPathResource resource = new ClassPathResource("fml/mcmod-unquoted-ver.info");

        FmlModInfo fmlModInfo;
        try (InputStream in = resource.getInputStream()) {
            fmlModInfo = modsService.extractFmlModInfo(in);
        }

        assertNotNull(fmlModInfo);
        assertThat(fmlModInfo.getModList(), hasSize(1));
        assertEquals("Parachute Mod", fmlModInfo.getModList().get(0).getName());
        assertEquals("1.7.10-2.5.6", fmlModInfo.getModList().get(0).getVersion());
    }

    @Test
    public void testMissingCommasBetweenFields() throws Exception {
        final ClassPathResource resource = new ClassPathResource("fml/oreseeds-mcmod.info");

        FmlModInfo fmlModInfo;
        try (InputStream in = resource.getInputStream()) {
            fmlModInfo = modsService.extractFmlModInfo(in);
        }

        assertNotNull(fmlModInfo);
        assertThat(fmlModInfo.getModList(), hasSize(1));
        assertEquals("Ore Seeds", fmlModInfo.getModList().get(0).getName());
    }

    @Test
    public void testProcessFmlManifest() throws Exception {
        Holder<RegisteredMod> holder = new Holder<>();
        final ByteArrayInputStream in = new ByteArrayInputStream(("Manifest-Version: 1.0\n" +
                "FMLCorePlugin: TMIForgeLoader\n").getBytes());
        modsService.processManifest(holder, in, "TooManyItems2015_02_14_1.8_Forge.jar");

        assertTrue(holder.isSet());
        assertEquals("TooManyItems", holder.get().getName());
        assertEquals("2015_02_14_1.8_Forge", holder.get().getVersion());
    }

    @Test
    public void testBulkSuite() throws Exception {
        final String bulkModsDir = System.getProperty("mccy.bulk.mods");
        Assume.assumeTrue(bulkModsDir != null);

        @SuppressWarnings("ConstantConditions") final Path modsPath = Paths.get(bulkModsDir);
        Assume.assumeTrue(Files.isDirectory(modsPath));

        Files.walk(modsPath)
                .filter(path -> path.getFileName().toString().endsWith(".jar"))
                .forEach(path -> {
                    try {
                        final String filename = path.getFileName().toString();
                        final MockMultipartFile mockMultipartFile =
                                new MockMultipartFile(filename, filename, "application/java-archive",
                                        Files.newInputStream(path));
                        final RegisteredMod mod = modsService.ingest(mockMultipartFile);
                        assertNotNull(mod);

                        Path infoPath = path.resolveSibling(filename+".json");
                        if (!Files.exists(infoPath)) {
                            try (BufferedWriter writer = Files.newBufferedWriter(infoPath)) {
                                objectMapper.writeValue(writer, mod);
                            }
                        }

                    } catch (MccyException | Exception e) {
                        fail("Exception during processing of "+path+":"+e);
                    }
                });



    }
}
