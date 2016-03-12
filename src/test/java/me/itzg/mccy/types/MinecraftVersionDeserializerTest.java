package me.itzg.mccy.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.itzg.mccy.model.WorldDescriptor;
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

        final WorldDescriptor
                worldDescriptor = objectMapper.readValue("{\"minecraftVersion\":\"1.8.8\"}", WorldDescriptor.class);
        assertEquals("1.8.8", worldDescriptor.getMinecraftVersion().toString());
    }

    @Test
    public void testSnapshotStyle() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final WorldDescriptor
                worldDescriptor = objectMapper.readValue("{\"minecraftVersion\":\"15w31b\"}", WorldDescriptor.class);
        assertEquals("15w31b", worldDescriptor.getMinecraftVersion().toString());
    }
}