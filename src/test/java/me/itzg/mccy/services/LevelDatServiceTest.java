package me.itzg.mccy.services;

import me.itzg.mccy.model.FmlModRef;
import me.itzg.mccy.model.WorldDescriptor;
import me.itzg.mccy.model.ModRef;
import me.itzg.mccy.model.ServerType;
import me.itzg.mccy.services.assets.LevelDatService;
import me.itzg.mccy.services.assets.impl.LevelDatServiceImpl;
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
        final WorldDescriptor worldDescriptor = loadLevelDescriptor("1.7.10-vanilla.nbt");

        assertNotNull(worldDescriptor);
        assertEquals("Herobrine's Mansion by Hypixel", worldDescriptor.getName());
        assertEquals(ServerType.VANILLA, worldDescriptor.getServerType());
        assertEquals("1.7", worldDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void test_1_8_vanilla() throws Exception, MccyException {
        final WorldDescriptor worldDescriptor = loadLevelDescriptor("1.8-vanilla.nbt");

        assertNotNull(worldDescriptor);
        assertEquals("world", worldDescriptor.getName());
        assertEquals(ServerType.VANILLA, worldDescriptor.getServerType());
        assertEquals("1.8", worldDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void test_pre_1_9_snapshot() throws Exception, MccyException {
        final WorldDescriptor worldDescriptor = loadLevelDescriptor("pre-1.9-snapshot.nbt");

        assertNotNull(worldDescriptor);
        assertEquals("MC Container Yard", worldDescriptor.getName());
        assertEquals(ServerType.SNAPSHOT, worldDescriptor.getServerType());
        assertEquals("15w51b", worldDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void test_1_7_forge() throws Exception, MccyException {
        final WorldDescriptor worldDescriptor = loadLevelDescriptor("1.7.10-with-forge-mods.nbt");

        assertNotNull(worldDescriptor);
        assertEquals("Alan Lightning World", worldDescriptor.getName());
        assertEquals(ServerType.FORGE, worldDescriptor.getServerType());
        assertEquals("1.7", worldDescriptor.getMinecraftVersion().toString());
        assertThat(worldDescriptor.getRequiredMods(), Matchers.hasSize(7));

        final List<ModRef> requiredMods = worldDescriptor.getRequiredMods();
        final ModRef modRef = requiredMods.get(0);
        assertThat(modRef, Matchers.instanceOf(FmlModRef.class));
        final FmlModRef fmlModRef = (FmlModRef) modRef;
        assertEquals("mcp", fmlModRef.getId());
        assertEquals("9.5", fmlModRef.getVersion().toString());

    }

    private WorldDescriptor loadLevelDescriptor(String levelFile) throws IOException, MccyException {
        final LevelDatService service = new LevelDatServiceImpl();

        final ClassPathResource levelDatResource = new ClassPathResource("level.dat/" + levelFile);

        final InputStream in = levelDatResource.getInputStream();
        final WorldDescriptor worldDescriptor = service.interpret(in);
        in.close();
        return worldDescriptor;
    }
}