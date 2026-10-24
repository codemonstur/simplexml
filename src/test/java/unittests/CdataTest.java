package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.jupiter.api.Assertions.*;

public class CdataTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void cdataIsReadAsText() {
        assertEquals("hi", text("<a><![CDATA[hi]]></a>"), "CDATA body was not read as text");
    }

    @Test
    public void entitiesAreNotDecoded() {
        assertEquals("a &amp; b", text("<a><![CDATA[a &amp; b]]></a>"), "Entities in CDATA were decoded");
    }

    @Test
    public void markupIsLiteral() {
        assertEquals("<b/>", text("<a><![CDATA[<b/>]]></a>"), "Markup in CDATA was parsed");
    }

    @Test
    public void greaterThanInsideBody() {
        assertEquals("a > b", text("<a><![CDATA[a > b]]></a>"), "CDATA body was cut at the first '>'");
    }

    @Test
    public void greaterThanFirstInBody() {
        assertEquals(">", text("<a><![CDATA[>]]></a>"), "CDATA body was cut at the first '>'");
    }

    @Test
    public void textSurroundingCdata() {
        assertEquals("xyz", text("<a>x<![CDATA[y]]>z</a>"), "Text around CDATA was lost");
    }

    @Test
    public void emptyCdata() {
        assertEquals("|", text("<a><![CDATA[]]>|</a>"), "Empty CDATA was not ignored");
    }

    @Test
    public void bracketsBeforeTerminator() {
        assertEquals("a]]b", text("<a><![CDATA[a]]b]]></a>"), "CDATA body was cut at the first ']'");
    }

    @Test
    public void whitespaceIsPreserved() {
        assertEquals("  hi  ", text("<a><![CDATA[  hi  ]]></a>"), "CDATA whitespace was trimmed");
    }

    @Test
    public void nestedElementHoldsCdata() {
        final var actual = parser.fromXml("<a><b><![CDATA[<c/>]]></b></a>");

        assertEquals("<c/>", actual.findChildForName("b", null).getText(),
                "CDATA in a child element was lost");
    }

    @Test
    public void deserializeCdataIntoField() {
        final var actual = parser.fromXml("<pojo><name><![CDATA[<x>]]></name></pojo>", Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("<x>", actual.name, "CDATA was not deserialized into the field");
    }

    @Test
    public void unterminatedCdata() {
        assertThrows(InvalidXml.class, () -> parser.fromXml("<a><![CDATA[abc</a>"));
    }

    @Test
    public void cdataBeforeFirstTag() {
        assertThrows(InvalidXml.class, () -> parser.fromXml("<![CDATA[x]]><a/>"),
                "CDATA before the opening tag not detected");
    }

    @Test
    public void compressPreservesCdata() {
        assertEquals("<a><![CDATA[hi]]></a>", parser.compressXml("<a> <![CDATA[hi]]> </a>"),
                "CDATA was not preserved when compressing");
    }

    @Test
    public void compressPreservesCdataContainingMarkup() {
        assertEquals("<a><![CDATA[<b/>]]></a>", parser.compressXml("<a><![CDATA[<b/>]]></a>"),
                "CDATA containing markup was rewritten");
    }

    @Test
    public void compressPreservesCdataContainingGreaterThan() {
        assertEquals("<a><![CDATA[a > b]]></a>", parser.compressXml("<a><![CDATA[a > b]]></a>"),
                "CDATA containing '>' was truncated");
    }

    private String text(final String xml) {
        return parser.fromXml(xml).getText();
    }

}
