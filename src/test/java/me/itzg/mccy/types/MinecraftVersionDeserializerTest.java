package me.itzg.mccy.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.mccy.model.LevelDescriptor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class MinecraftVersionDeserializerTest {

    @Test
    public void testDottedVersion() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final LevelDescriptor levelDescriptor = objectMapper.readValue("{\"minecraftVersion\":\"1.8.8\"}", LevelDescriptor.class);
        assertEquals("1.8.8", levelDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void testSnapshotStyle() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final LevelDescriptor levelDescriptor = objectMapper.readValue("{\"minecraftVersion\":\"15w31b\"}", LevelDescriptor.class);
        assertEquals("15w31b", levelDescriptor.getMinecraftVersion().toString());
    }
}