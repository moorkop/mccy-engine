package me.itzg.mccy.types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class HolderTest {
    @Test
    public void testTypicalCase() throws Exception {
        Holder<String> h = new Holder<>();
        assertFalse(h.isSet());

        final String content = "content";
        h.set(content);
        assertTrue(h.isSet());
        assertSame(content, h.get());
    }

    @Test
    public void testGetOrCreate() throws Exception {
        Holder<String> h = new Holder<>();

        h.getOrCreate(() -> "adhoc");

        assertEquals("adhoc", h.get());

    }

    @Test
    public void testIsInstanceOf() throws Exception {
        Holder<Number> h = new Holder<>();

        h.set(new Long(123));
        assertTrue(h.isInstanceOf(Number.class));
        assertTrue(h.isInstanceOf(Long.class));
        assertFalse(h.isInstanceOf(Integer.class));

    }
}