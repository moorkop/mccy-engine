package me.itzg.mccy.types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Geoff Bourne
 * @since 12/31/2015
 */
public class ComparableVersionTest {

    @Test
    public void testCompareTo() throws Exception {
        assertEquals(-1, new ComparableVersion("1.8").compareTo(new ComparableVersion("1.8.1")));
        assertEquals(0, new ComparableVersion("1.8.1").compareTo(new ComparableVersion("1.8.1")));
        assertEquals(1, new ComparableVersion("1.9.1").compareTo(new ComparableVersion("1.8.1")));
        assertEquals(-1, new ComparableVersion("1.9.1").compareTo(new ComparableVersion("2.0")));
    }

    @Test
    public void testTrimToString() throws Exception {
        assertEquals("1.8", new ComparableVersion("1.8.1").trimToString(2));
        assertEquals("1.8", new ComparableVersion("1.8").trimToString(2));
        assertEquals("1", new ComparableVersion("1").trimToString(2));
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("1.8.1", new ComparableVersion("1.8.1").toString());
        assertEquals("1.8", new ComparableVersion("1.8").toString());
        assertEquals("1", new ComparableVersion("1").toString());
    }

    @Test
    public void testTrim() throws Exception {
        assertEquals("1.8", new ComparableVersion("1.8.1").trim(2).toString());
    }
}