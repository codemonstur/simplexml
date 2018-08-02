package example;

import simplexml.SimpleXml;
import simplexml.utils.Interfaces.CheckedIterator;

import java.io.InputStream;

public final class RawStream {

    public static void main(final String... args) throws Exception {
        final SimpleXml simple = new SimpleXml();

        try (final InputStream in = RawStream.class.getResourceAsStream("/streamable_2.xml")) {
            final CheckedIterator<String> it = simple.iterateXml(in);
            while (it.hasNext()) {
                System.out.println("Got an item: " + it.next());
            }
        }
    }

}
