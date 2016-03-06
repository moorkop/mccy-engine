package me.itzg.mccy.types;

import java.util.UUID;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class SimpleUUIDGenerator implements UUIDGenerator {
    public static final String PREFIX = "aaaaaaaa-bbbb-cccc-dddd-";
    int i = 1;

    @Override
    public UUID generate() {
        return UUID.fromString(PREFIX + "0000000" + (i++));
    }
}
