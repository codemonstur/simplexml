package unittests;

import model.SimplePojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StreamTest {

    private static final String STREAMABLE =
            "<tag attribute=\"something\" bla=\"/\">\n" +
            "    <child></child>\n" +
            "</tag>\n" +
            "\n" +
            "<something />\n" +
            "\n" +
            "<else><child1></child1><child2></child2></else>\n";

    private static final String STREAMABLE_OBJECTS =
            "<simplepojo>\n" +
            "    <integer>1</integer>\n" +
            "</simplepojo>\n" +
            "\n" +
            "<simplepojo />\n" +
            "\n" +
            "<simplepojo><string>test</string></simplepojo>\n";

    private static final String INVALID_XML_STREAM =
            "<tag attribute=\"something\" bla=\"/\">\n" +
            "    <child></child>\n" +
            "</tag>\n" +
            "\n" +
            "<" +
            "\n" ;

    private static final String XML_ITEM_1 =
            "<tag attribute=\"something\" bla=\"/\">\n" +
            "    <child></child>\n" +
            "</tag>";
    private static final String XML_ITEM_2 = "<something />";
    private static final String XML_ITEM_3 = "<else><child1></child1><child2></child2></else>";

    private final XmlParser parser = new XmlParser();

    @Test
    public void streamRaw() throws Exception {

        final var actual = new ArrayList<String>();
        try (final var in = new ByteArrayInputStream(STREAMABLE.getBytes(UTF_8))) {
            final var it = parser.iterateXml(in);
            while (it.hasNext()) {
                actual.add(it.next());
            }
        }

        assertEquals("Number of items is incorrect", 3, actual.size());
        assertEquals("Item 1 is not equal", XML_ITEM_1, actual.get(0));
        assertEquals("Item 2 is not equal", XML_ITEM_2, actual.get(1));
        assertEquals("Item 3 is not equal", XML_ITEM_3, actual.get(2));
    }

    @Test
    public void streamDom() throws Exception {

        final var actual = new ArrayList<XmlElement>();
        try (final var in = new ByteArrayInputStream(STREAMABLE.getBytes(UTF_8))) {
            final var it = parser.iterateDom(in);
            while (it.hasNext()) {
                actual.add(it.next());
            }
        }

        assertEquals("Number of items is incorrect", 3, actual.size());
        assertEquals("Root tag of item 1 is incorrect", "tag", actual.get(0).name);
        assertEquals("Root tag of item 2 is incorrect", "something", actual.get(1).name);
        assertEquals("Root tag of item 3 is incorrect", "else", actual.get(2).name);
    }

    @Test
    public void streamObject() throws Exception {

        final var actual = new ArrayList<SimplePojo>();
        try (final var in = new ByteArrayInputStream(STREAMABLE_OBJECTS.getBytes(UTF_8))) {
            final var it = parser.iterateObject(in, SimplePojo.class);
            while (it.hasNext()) {
                actual.add(it.next());
            }
        }

        assertEquals("Number of items is incorrect", 3, actual.size());
        assertEquals("Item 1 is incorrect", Integer.valueOf(1), actual.get(0).integer);
        assertNotNull("Item 2 is null", actual.get(1));
        assertEquals("Item 2 is incorrect", "test", actual.get(2).string);
    }

    @Test(expected = InvalidXml.class)
    public void invalidXmlStreamRawThrowsException() throws Exception {
        try (final var in = new ByteArrayInputStream(INVALID_XML_STREAM.getBytes(UTF_8))) {
            final var it = parser.iterateXml(in);
            while (it.hasNext()) {}
        }
    }

    @Test(expected = InvalidXml.class)
    public void invalidXmlStreamDomThrowsException() throws Exception {
        try (final var in = new ByteArrayInputStream(INVALID_XML_STREAM.getBytes(UTF_8))) {
            final var it = parser.iterateDom(in);
            while (it.hasNext()) {}
        }
    }

    @Test(expected = InvalidXml.class)
    public void invalidXmlStreamObjectThrowsException() throws Exception {
        try (final var in = new ByteArrayInputStream(INVALID_XML_STREAM.getBytes(UTF_8))) {
            final var it = parser.iterateObject(in, SimplePojo.class);
            while (it.hasNext()) {}
        }
    }

}
