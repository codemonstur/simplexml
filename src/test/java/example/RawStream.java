package example;

import xmlparser.XmlParser;
import xmlparser.utils.Interfaces.CheckedIterator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class RawStream {

    private static final String STREAMABLE = "<tag>\n" +
            "    <child></child>\n" +
            "</tag>\n" +
            "\n" +
            "<something/>\n" +
            "\n" +
            "<else>\n" +
            "    <child1></child1>\n" +
            "    <child2></child2>\n" +
            "</else>\n";

    public static void main(final String... args) throws Exception {
        final XmlParser parser = new XmlParser();

        try (final InputStream in = new ByteArrayInputStream(STREAMABLE.getBytes(UTF_8))) {
            final CheckedIterator<String> it = parser.iterateXml(in);
            while (it.hasNext()) {
                System.out.println("Got an item: " + it.next());
            }
        }
    }

}
