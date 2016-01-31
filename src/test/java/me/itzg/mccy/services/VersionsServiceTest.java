package me.itzg.mccy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.mccy.config.MccyVersionSettings;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.ComparableVersion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Unit test of the {@link VersionsService}
 *
 * Created by geoff on 12/27/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
public class VersionsServiceTest {

    @Configuration
    public static class TestConfig {
        @Bean
        public VersionsService versionsService() {
            return new VersionsService();
        }

        @Bean
        public MccyVersionSettings settings() throws IOException {
            final MccyVersionSettings mccySettings = new MccyVersionSettings();
            mccySettings.setOfficialVersionsUri(
                    new ClassPathResource("versions.json").getURI());

            return mccySettings;
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private VersionsService versionsService;

    @Test
    public void testGetVanillaVersions() throws Exception {
        final List<ComparableVersion> versions = versionsService.getVersions(ServerType.VANILLA);

        assertNotNull(versions);
        assertThat(versions, not(emptyIterable()));
        assertEquals(ComparableVersion.of("1.8.9"), versions.iterator().next());
    }

    @Test
    public void testForgeVersions() throws Exception {
        final List<ComparableVersion> versions = versionsService.getVersions(ServerType.FORGE);

        assertEquals(8, versions.size());

        assertEquals(ComparableVersion.of("1.8.9"), versions.get(0));
        assertEquals(ComparableVersion.of("1.8.8"), versions.get(1));
        assertEquals(ComparableVersion.of("1.8"), versions.get(2));
        assertEquals(ComparableVersion.of("1.7.10"), versions.get(3));
        assertEquals(ComparableVersion.of("1.7.2"), versions.get(7));
    }

    @Test
    public void testBukkitVersions() throws Exception {
        final List<ComparableVersion> versions = versionsService.getVersions(ServerType.BUKKIT);

        assertEquals(1, versions.size());

        assertEquals(ComparableVersion.of("1.8"), versions.get(0));

    }
}