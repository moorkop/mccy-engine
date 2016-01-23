package me.itzg.mccy.services;

import me.itzg.mccy.config.GeneralConfig;
import me.itzg.mccy.model.ContainerSummary;
import me.itzg.mccy.model.LevelType;
import me.itzg.mccy.model.ServerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 1/23/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({
        GeneralConfig.class,
        MetadataConversionService.class
})
public class MetadataConversionServiceTest {

    @Autowired
    private MetadataConversionService service;

    @Test
    public void testFillFromEnv() throws Exception {

        final List<String> envList = Arrays.asList(
                "VERSION=1.8.9",
                "ICON=http://localhost/icon.png",
                "WORLD=http://www.example.com/worlds/MySave.zip",
                "MODPACK=http://www.example.com/mods/modpack.zip",
                "OPS=itzg,enenbee",
                "WHITELIST=user1,user2",
                "FORGEVERSION=10.13.4.1448",
                "TYPE=FORGE",
                "DIFFICULTY=hard",
                "SEED=1785852800490497919",
                "MODE=creative",
                "MOTD=My Server",
                "LEVEL_TYPE=FLAT",
                "GENERATOR_SETTINGS=3;minecraft:bedrock,3*minecraft:stone,52*minecraft:sandstone;2;",
                "LEVEL=bonus",
                "PVP=false"
        );
        ContainerSummary containerSummary = new ContainerSummary();

        service.fillFromEnv(envList, containerSummary);

        assertEquals("1.8.9", containerSummary.getVersion());
        assertEquals(URI.create("http://localhost/icon.png"), containerSummary.getIcon());
        assertEquals(URI.create("http://www.example.com/worlds/MySave.zip"), containerSummary.getWorld());
        assertEquals(URI.create("http://www.example.com/mods/modpack.zip"), containerSummary.getModpack());
        assertArrayEquals(new String[]{"itzg","enenbee"}, containerSummary.getOps());
        assertArrayEquals(new String[]{"user1","user2"}, containerSummary.getWhitelist());
        assertEquals("10.13.4.1448", containerSummary.getForgeVersion());
        assertEquals(ServerType.FORGE, containerSummary.getType());
        assertEquals("hard", containerSummary.getDifficulty());
        assertEquals("1785852800490497919", containerSummary.getSeed());
        assertEquals("creative", containerSummary.getMode());
        assertEquals("My Server", containerSummary.getMotd());
        assertEquals(LevelType.FLAT, containerSummary.getLevelType());
        assertEquals("3;minecraft:bedrock,3*minecraft:stone,52*minecraft:sandstone;2;",
                containerSummary.getGeneratorSettings());
        assertEquals("bonus", containerSummary.getLevel());
        assertEquals(Boolean.FALSE, containerSummary.getPvp());
    }
}