package me.itzg.mccy.types;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Geoff Bourne
 * @since 0.1
 */
public class ComparableVersion implements Comparable<ComparableVersion> {
    private final List<Object> parts;

    private final boolean dotted;
    private final String originalVersion;

    public ComparableVersion(String rawVersion) {
        dotted = true;
        originalVersion = rawVersion;
        parts = Stream.of(rawVersion.split("\\."))
                .map(ComparableVersion::promote)
                .collect(Collectors.toList());
    }

    public ComparableVersion(String rawVersion, String pattern) {
        dotted = false;
        this.originalVersion = rawVersion;

        final Pattern compiled = Pattern.compile(pattern);
        final Matcher matcher = compiled.matcher(rawVersion);
        if (matcher.matches()) {
            final int partCount = matcher.groupCount();
            parts = new ArrayList<>(partCount);
            for (int i = 0; i < partCount; i++) {
                parts.add(matcher.group(i+1));
            }
        }
        else {
            throw new IllegalArgumentException("The given raw version " + rawVersion + " does not match the given pattern, " + pattern);
        }
    }

    private ComparableVersion(List<Object> parts) {
        dotted = true;
        originalVersion = null;
        this.parts = parts;
    }

    public static ComparableVersion of(String rawVersion) {
        return new ComparableVersion(rawVersion);
    }

    public static ComparableVersion of(String rawVersion, String pattern) {
        return new ComparableVersion(rawVersion, pattern);
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
        if (!dotted) {
            throw new IllegalStateException("Non-dotted versions cannot be trimmed");
        }
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
        if (!dotted) {
            throw new IllegalStateException("Non-dotted versions cannot be trimmed");
        }
        return new ComparableVersion(parts.subList(0, leadingParts));
    }

    @JsonValue
    @Override
    public String toString() {
        return !dotted ? originalVersion :
                parts.stream()
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
