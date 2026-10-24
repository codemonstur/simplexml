package unittests;

import model.SimplePojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class StreamTest {

    private static final String STREAMABLE = """
            <tag attribute="something" bla="/">
                <child></child>
            </tag>
            
            <something />
            
            <else><child1></child1><child2></child2></else>
            """;

    private static final String STREAMABLE_OBJECTS = """
            <simplepojo>
                <integer>1</integer>
            </simplepojo>
            
            <simplepojo />
            
            <simplepojo><string>test</string></simplepojo>
            """;

    private static final String INVALID_XML_STREAM = """
            <tag attribute="something" bla="/">
                <child></child>
            </tag>
            
            <\
            
            """;

    private static final String XML_ITEM_1 = """
            <tag attribute="something" bla="/">
                <child></child>
            </tag>""";
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

        assertEquals(3, actual.size(), "Number of items is incorrect");
        assertEquals(XML_ITEM_1, actual.get(0), "Item 1 is not equal");
        assertEquals(XML_ITEM_2, actual.get(1), "Item 2 is not equal");
        assertEquals(XML_ITEM_3, actual.get(2), "Item 3 is not equal");
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

        assertEquals(3, actual.size(), "Number of items is incorrect");
        assertEquals("tag", actual.get(0).name, "Root tag of item 1 is incorrect");
        assertEquals("something", actual.get(1).name, "Root tag of item 2 is incorrect");
        assertEquals("else", actual.get(2).name, "Root tag of item 3 is incorrect");
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

        assertEquals(3, actual.size(), "Number of items is incorrect");
        assertEquals(Integer.valueOf(1), actual.get(0).integer, "Item 1 is incorrect");
        assertNotNull(actual.get(1), "Item 2 is null");
        assertEquals("test", actual.get(2).string, "Item 2 is incorrect");
    }

    @Test
    public void invalidXmlStreamRawThrowsException() {
        assertThrows(InvalidXml.class, () -> {
            try (final var in = new ByteArrayInputStream(INVALID_XML_STREAM.getBytes(UTF_8))) {
                final var it = parser.iterateXml(in);
                while (it.hasNext()) it.next();
            }
        });
    }

    @Test
    public void invalidXmlStreamDomThrowsException() {
        assertThrows(InvalidXml.class, () -> {
            try (final var in = new ByteArrayInputStream(INVALID_XML_STREAM.getBytes(UTF_8))) {
                final var it = parser.iterateDom(in);
                while (it.hasNext()) it.next();
            }
        });
    }

    @Test
    public void invalidXmlStreamObjectThrowsException() {
        assertThrows(InvalidXml.class, () -> {
            try (final var in = new ByteArrayInputStream(INVALID_XML_STREAM.getBytes(UTF_8))) {
                final var it = parser.iterateObject(in, SimplePojo.class);
                while (it.hasNext()) it.next();
            }
        });
    }

    @Test
    public void streamRawWithASectionInsideAnElement() throws Exception {
        final var actual = readAllRaw("<a><![CDATA[<b/>]]></a><c>x</c>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a><![CDATA[<b/>]]></a>", actual.get(0), "Item 1 is not equal");
        assertEquals("<c>x</c>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithASectionHoldingTheEndOfItsElement() throws Exception {
        final var actual = readAllRaw("<a><d><![CDATA[</d><e>]]></d></a><c>x</c>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a><d><![CDATA[</d><e>]]></d></a>", actual.get(0), "Item 1 is not equal");
        assertEquals("<c>x</c>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithACommentInsideAnElement() throws Exception {
        final var actual = readAllRaw("<a><!-- <b/> --></a><c>x</c>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a><!-- <b/> --></a>", actual.get(0), "Item 1 is not equal");
        assertEquals("<c>x</c>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithACommentHoldingAGreaterThan() throws Exception {
        final var actual = readAllRaw("<a><!-- a > b --></a><c>x</c>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a><!-- a > b --></a>", actual.get(0), "Item 1 is not equal");
        assertEquals("<c>x</c>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithACommentBetweenElements() throws Exception {
        final var actual = readAllRaw("<a/><!-- <b/> --><b/>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a/>", actual.get(0), "Item 1 is not equal");
        assertEquals("<b/>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithAProlog() throws Exception {
        final var actual = readAllRaw("<?xml version=\"1.0\"?><a>x</a><b>y</b>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a>x</a>", actual.get(0), "Item 1 is not equal");
        assertEquals("<b>y</b>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithAProcessingInstructionBetweenElements() throws Exception {
        final var actual = readAllRaw("<a/><?pi x?><b/>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a/>", actual.get(0), "Item 1 is not equal");
        assertEquals("<b/>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawWithADeclarationInsideAnElement() throws Exception {
        final var actual = readAllRaw("<a><!DOCTYPE html></a><b/>");

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<a><!DOCTYPE html></a>", actual.get(0), "Item 1 is not equal");
        assertEquals("<b/>", actual.get(1), "Item 2 is not equal");
    }

    @Test
    public void streamRawHoldingNothingButAComment() throws Exception {
        final var actual = readAllRaw("<!-- nothing else -->");

        assertEquals(1, actual.size(), "Number of items is incorrect");
        assertEquals("<!-- nothing else -->", actual.get(0), "Item 1 is not equal");
    }

    @Test
    public void streamRawWithUnbalancedTagsThrowsException() throws Exception {
        try (final var in = new ByteArrayInputStream("<a></b></a>".getBytes(UTF_8))) {
            final var it = parser.iterateXml(in);
            while (it.hasNext()) it.next();

            fail("Unbalanced tags were not caught");
        } catch (final InvalidXml e) {
            assertEquals("Unbalanced tags: closing tag without a matching opening tag",
                    e.getMessage(), "Invalid error message");
        }
    }

    @Test
    public void streamDomWithASectionInsideAnElement() throws Exception {
        final var actual = new ArrayList<XmlElement>();
        try (final var in = new ByteArrayInputStream("<a><![CDATA[<b/>]]></a><c>x</c>".getBytes(UTF_8))) {
            final var it = parser.iterateDom(in);
            while (it.hasNext()) actual.add(it.next());
        }

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("a", actual.get(0).name, "Root tag of item 1 is incorrect");
        assertEquals("c", actual.get(1).name, "Root tag of item 2 is incorrect");
    }

    @Test
    public void streamObjectWithASectionInsideAnElement() throws Exception {
        final var actual = new ArrayList<SimplePojo>();
        try (final var in = new ByteArrayInputStream(
                ("<simplepojo><string><![CDATA[<x>]]></string></simplepojo>" +
                 "<simplepojo><string>2</string></simplepojo>").getBytes(UTF_8))) {
            final var it = parser.iterateObject(in, SimplePojo.class);
            while (it.hasNext()) actual.add(it.next());
        }

        assertEquals(2, actual.size(), "Number of items is incorrect");
        assertEquals("<x>", actual.get(0).string, "Item 1 is incorrect");
        assertEquals("2", actual.get(1).string, "Item 2 is incorrect");
    }

    private ArrayList<String> readAllRaw(final String input) throws Exception {
        final var actual = new ArrayList<String>();
        try (final var in = new ByteArrayInputStream(input.getBytes(UTF_8))) {
            final var it = parser.iterateXml(in);
            while (it.hasNext()) actual.add(it.next());
        }
        return actual;
    }

}
