package example;

import xmlparser.XmlParser;
import xmlparser.utils.Interfaces.CheckedIterator;

import java.io.InputStream;

public final class RawStream {

    public static void main(final String... args) throws Exception {
        final XmlParser parser = new XmlParser();

        try (final InputStream in = RawStream.class.getResourceAsStream("/streamable_2.xml")) {
            final CheckedIterator<String> it = parser.iterateXml(in);
            while (it.hasNext()) {
                System.out.println("Got an item: " + it.next());
            }
        }
    }

}
