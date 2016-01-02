package me.itzg.mccy.types;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Geoff Bourne
 * @since 12/31/2015
 */
public class ComparableVersion implements Comparable<ComparableVersion> {
    private final List<Object> parts;

    public ComparableVersion(String rawVersion) {
        parts = Stream.of(rawVersion.split("\\."))
                .map(ComparableVersion::promote)
                .collect(Collectors.toList());
    }

    private ComparableVersion(List<Object> parts) {
        this.parts = parts;
    }

    @Override
    public boolean equals(Object obj) {
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

    public String trimToString(int leadingParts) {
        return parts.stream()
                .limit(leadingParts)
                .map(Object::toString)
                .collect(Collectors.joining("."));
    }

    public ComparableVersion trim(int leadingParts) {
        return new ComparableVersion(
                parts.stream()
                .limit(leadingParts)
                .collect(Collectors.toList()));
    }

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
