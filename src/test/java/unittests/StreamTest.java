package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.utils.Interfaces.CheckedIterator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class StreamTest {

    private static final String STREAMABLE =
            "<tag attribute=\"something\" bla=\"/\">\n" +
            "    <child></child>\n" +
            "</tag>\n" +
            "\n" +
            "<something />\n" +
            "\n" +
            "<else><child1></child1><child2></child2></else>\n";

    private static final String XML_ITEM_1 =
            "<tag attribute=\"something\" bla=\"/\">\n" +
            "    <child></child>\n" +
            "</tag>";
    private static final String XML_ITEM_2 = "<something />";
    private static final String XML_ITEM_3 = "<else><child1></child1><child2></child2></else>";

    @Test
    public void streamRaw() throws Exception {
        final XmlParser parser = new XmlParser();

        final List<String> list = new ArrayList<>();
        try (final InputStream in = new ByteArrayInputStream(STREAMABLE.getBytes(UTF_8))) {
            final CheckedIterator<String> it = parser.iterateXml(in);
            while (it.hasNext()) {
                list.add(it.next());
            }
        }

        assertEquals("Item 1 is not equal", XML_ITEM_1, list.get(0));
        assertEquals("Item 2 is not equal", XML_ITEM_2, list.get(1));
        assertEquals("Item 3 is not equal", XML_ITEM_3, list.get(2));
    }

}
