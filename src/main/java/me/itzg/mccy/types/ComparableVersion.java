package me.itzg.mccy.types;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class ComparableVersion implements Comparable<ComparableVersion> {
    private final List<Object> parts;

    public ComparableVersion(String rawVersion) {
        parts = Stream.of(rawVersion.split("\\."))
                .map(ComparableVersion::promote)
                .collect(Collectors.toList());
    }

    public static ComparableVersion of(String rawVersion) {
        return new ComparableVersion(rawVersion);
    }

    private ComparableVersion(List<Object> parts) {
        this.parts = parts;
    }

    @Override
    public boolean equals(Object obj) {
        //noinspection SimplifiableIfStatement
        if (obj instanceof ComparableVersion) {
            return Objects.equals(parts, ((ComparableVersion) obj).parts);
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return parts.hashCode();
    }

    @Override
    public int compareTo(ComparableVersion o) {

        final int minLen = Math.min(parts.size(), o.parts.size());

        for (int i = 0; i < minLen; i++) {
            final int comparison;
            if (parts.get(i) instanceof Long && o.parts.get(i) instanceof Long) {
                comparison = ((Long) parts.get(i)).compareTo(((Long) o.parts.get(i)));
            }
            else {
                comparison = (parts.get(i).toString()).compareTo((o.parts.get(i).toString()));
            }

            if (comparison != 0) {
                return comparison;
            }
        }

        // ...shorter parts means lower version, such as 1.8 < 1.8.1
        return Long.compare(parts.size(), o.parts.size());
    }

    public boolean le(ComparableVersion o) {
        return this.compareTo(o) <= 0;
    }
    public boolean lt(ComparableVersion o) {
        return this.compareTo(o) < 0;
    }
    public boolean eq(ComparableVersion o) {
        return this.compareTo(o) == 0;
    }
    public boolean gt(ComparableVersion o) {
        return this.compareTo(o) > 0;
    }
    public boolean ge(ComparableVersion o) {
        return this.compareTo(o) >= 0;
    }

    /**
     * This performs the same logic as {@link #trim(int)} put directly computes the resulting string. If you're
     * needing a string in the end, then this is slightly more efficient since an intermediate object is not
     * created.
     * @param leadingParts the number of leading parts of the version to include in the returned value
     * @return the trimmed version as a string
     * @see #trim(int)
     */
    public String trimToString(int leadingParts) {
        return parts.stream()
                .limit(leadingParts)
                .map(Object::toString)
                .collect(Collectors.joining("."));
    }

    /**
     * This takes the given version and potentially trims it back to no more than the given number of leading parts.
     * <p>
     *     For example, a given version of 1.8.1 with <code>leadingParts</code> of 2 would become 1.8.
     * </p>
     * @param leadingParts the number of leading parts of the version to include in the returned value
     * @return a new {@link ComparableVersion} that is trimmed back to just the number of parts specified by
     * <code>leadingParts</code>
     */
    public ComparableVersion trim(int leadingParts) {
        return new ComparableVersion(parts.subList(0, leadingParts));
    }

    @JsonValue
    @Override
    public String toString() {
        return parts.stream()
                .map(Object::toString)
                .collect(Collectors.joining("."));
    }

    private static Object promote(String s) {
        try {
            return new Long(s);
        } catch (NumberFormatException e) {
            return s;
        }
    }
}
