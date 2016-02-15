package me.itzg.mccy.services;

import me.itzg.mccy.model.FmlModRef;
import me.itzg.mccy.model.LevelDescriptor;
import me.itzg.mccy.model.ModRef;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.types.MccyException;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class LevelDatServiceTest {
    @Test
    public void test_1_7_10_vanilla() throws Exception, MccyException {
        final LevelDescriptor levelDescriptor = loadLevelDescriptor("1.7.10-vanilla.nbt");

        assertNotNull(levelDescriptor);
        assertEquals("Herobrine's Mansion by Hypixel", levelDescriptor.getName());
        assertEquals(ServerType.VANILLA, levelDescriptor.getServerType());
        assertEquals("1.7", levelDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void test_1_8_vanilla() throws Exception, MccyException {
        final LevelDescriptor levelDescriptor = loadLevelDescriptor("1.8-vanilla.nbt");

        assertNotNull(levelDescriptor);
        assertEquals("world", levelDescriptor.getName());
        assertEquals(ServerType.VANILLA, levelDescriptor.getServerType());
        assertEquals("1.8", levelDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void test_pre_1_9_snapshot() throws Exception, MccyException {
        final LevelDescriptor levelDescriptor = loadLevelDescriptor("pre-1.9-snapshot.nbt");

        assertNotNull(levelDescriptor);
        assertEquals("MC Container Yard", levelDescriptor.getName());
        assertEquals(ServerType.SNAPSHOT, levelDescriptor.getServerType());
        assertEquals("15w51b", levelDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void test_1_7_forge() throws Exception, MccyException {
        final LevelDescriptor levelDescriptor = loadLevelDescriptor("1.7.10-with-forge-mods.nbt");

        assertNotNull(levelDescriptor);
        assertEquals("Alan Lightning World", levelDescriptor.getName());
        assertEquals(ServerType.FORGE, levelDescriptor.getServerType());
        assertEquals("1.7", levelDescriptor.getMinecraftVersion().toString());
        assertThat(levelDescriptor.getRequiredMods(), Matchers.hasSize(7));

        final List<ModRef> requiredMods = levelDescriptor.getRequiredMods();
        final ModRef modRef = requiredMods.get(0);
        assertThat(modRef, Matchers.instanceOf(FmlModRef.class));
        final FmlModRef fmlModRef = (FmlModRef) modRef;
        assertEquals("mcp", fmlModRef.getId());
        assertEquals("9.5", fmlModRef.getVersion().toString());

    }

    private LevelDescriptor loadLevelDescriptor(String levelFile) throws IOException, MccyException {
        final LevelDatService service = new LevelDatService();

        final ClassPathResource levelDatResource = new ClassPathResource("level.dat/" + levelFile);

        final InputStream in = levelDatResource.getInputStream();
        final LevelDescriptor levelDescriptor = service.interpret(in);
        in.close();
        return levelDescriptor;
    }
}