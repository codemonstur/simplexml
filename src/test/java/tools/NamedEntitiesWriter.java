package tools;

import xmlparser.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static xmlparser.utils.Constants.NAMED_ENTITIES;

public class NamedEntitiesWriter {

    public static void main(final String... args) throws IOException {

//        final var targetFile = createParentDirectories(Paths.get("simplexml/target/named-entities-map"));
//        System.out.println("target: " + targetFile.toAbsolutePath());
//        writeMap(targetFile, NAMED_ENTITIES);

    }

    private static final HexFormat HEX = HexFormat.of();
    private static void writeMap(final Path targetFile, final Map<String, String> map) throws IOException {
        try (final var writer = newBufferedWriter(targetFile, CREATE, APPEND)) {
            for (final var entry : map.entrySet()) {
                writer.write(entry.getKey());
                writer.write(HEX.formatHex(entry.getValue().getBytes(UTF_8)));
                writer.newLine();
            }
        }
    }

    private static Map<String, String> loadMap(final Path targetFile) throws IOException {
        final var map = new HashMap<String, String>();

        try (final var reader = newBufferedReader(targetFile, UTF_8)) {
            String line; while ((line = reader.readLine()) != null) {

                final int offset = line.indexOf(';');
                final var key = line.substring(0, offset + 1);
                final var value = new String(HEX.parseHex(line.substring(offset+1)), UTF_8);
                map.put(key, value);
            }
        }

        return map;
    }

    private static Path createParentDirectories(final Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return path;
    }

}
