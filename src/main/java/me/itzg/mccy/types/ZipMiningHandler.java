package me.itzg.mccy.types;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public interface ZipMiningHandler {
    void handleZipContentFile(String path, InputStream in) throws IOException, MccyException;

    static ListBuilder listBuilder() {
        return new ListBuilder();
    }

    class Entry {
        private Pattern path;
        private ZipMiningHandler handler;

        public Entry(Pattern path, ZipMiningHandler handler) {
            this.path = path;
            this.handler = handler;
        }

        public Pattern getPath() {
            return path;
        }

        public ZipMiningHandler getHandler() {
            return handler;
        }
    }

    class ListBuilder {

        private List<Entry> entries = new ArrayList<>();

        public ListBuilder add(String pathRegex, ZipMiningHandler handler) {
            entries.add(new Entry(Pattern.compile(pathRegex), handler));
            return this;
        }

        public Optional<List<Entry>> build() {
            return Optional.of(entries);
        }

    }
}
