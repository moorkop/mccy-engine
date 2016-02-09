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

    @Test
    public void testShortcutComparisons() throws Exception {

        assertTrue(ComparableVersion.of("1.8").lt(ComparableVersion.of("1.8.1")));
        assertTrue(ComparableVersion.of("1.8").le(ComparableVersion.of("1.8.1")));
        assertTrue(ComparableVersion.of("1.8").le(ComparableVersion.of("1.8")));
        assertTrue(ComparableVersion.of("1.8").eq(ComparableVersion.of("1.8")));
        assertTrue(ComparableVersion.of("1.8.1").gt(ComparableVersion.of("1.8")));
        assertTrue(ComparableVersion.of("1.8.1").ge(ComparableVersion.of("1.8")));
        assertTrue(ComparableVersion.of("1.8.1").ge(ComparableVersion.of("1.8.1")));

        assertFalse(ComparableVersion.of("1.8.1").lt(ComparableVersion.of("1.8")));
        assertFalse(ComparableVersion.of("1.8.1").le(ComparableVersion.of("1.8")));
        assertFalse(ComparableVersion.of("1.8").le(ComparableVersion.of("1.7")));
        assertFalse(ComparableVersion.of("1.8.1").eq(ComparableVersion.of("1.8")));
        assertFalse(ComparableVersion.of("1.8").gt(ComparableVersion.of("1.8.1")));
        assertFalse(ComparableVersion.of("1.8").ge(ComparableVersion.of("1.8.1")));
        assertFalse(ComparableVersion.of("1.8.1").ge(ComparableVersion.of("1.9.2")));

    }

    @Test
    public void testPatterned() throws Exception {

        final String pattern = "(?<nYear>\\d+)w(?<nWeek>\\d+)(?<sRel>[a-z]+)";

        final ComparableVersion snapshotVer = ComparableVersion.of("15w51b", pattern);

        assertEquals("15w51b", snapshotVer.toString());

        assertTrue(ComparableVersion.of("15w51b", pattern).eq(ComparableVersion.of("15w51b", pattern)));
        assertTrue(ComparableVersion.of("15w51a", pattern).lt(ComparableVersion.of("15w51b", pattern)));
        assertTrue(ComparableVersion.of("15w50b", pattern).lt(ComparableVersion.of("15w51b", pattern)));
        assertTrue(ComparableVersion.of("14w51b", pattern).lt(ComparableVersion.of("15w51b", pattern)));

    }
}