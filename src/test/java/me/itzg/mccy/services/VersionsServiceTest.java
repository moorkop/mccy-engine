package me.itzg.mccy.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.mccy.config.MccyVersionSettings;
import me.itzg.mccy.model.ServerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Unit test of the {@link VersionsService}
 *
 * Created by geoff on 12/27/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
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

    @Autowired
    private VersionsService versionsService;

    @Test
    public void testGetVanillaVersions() throws Exception {
        final Collection<String> versions = versionsService.getVersions(ServerType.VANILLA);

        assertNotNull(versions);
        assertThat(versions, not(emptyIterable()));
        assertEquals("1.8.9", versions.iterator().next());
    }
}