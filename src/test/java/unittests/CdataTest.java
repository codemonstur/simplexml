package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CdataTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void cdataIsReadAsText() {
        assertEquals("CDATA body was not read as text", "hi", text("<a><![CDATA[hi]]></a>"));
    }

    @Test
    public void entitiesAreNotDecoded() {
        assertEquals("Entities in CDATA were decoded", "a &amp; b", text("<a><![CDATA[a &amp; b]]></a>"));
    }

    @Test
    public void markupIsLiteral() {
        assertEquals("Markup in CDATA was parsed", "<b/>", text("<a><![CDATA[<b/>]]></a>"));
    }

    @Test
    public void greaterThanInsideBody() {
        assertEquals("CDATA body was cut at the first '>'", "a > b", text("<a><![CDATA[a > b]]></a>"));
    }

    @Test
    public void greaterThanFirstInBody() {
        assertEquals("CDATA body was cut at the first '>'", ">", text("<a><![CDATA[>]]></a>"));
    }

    @Test
    public void textSurroundingCdata() {
        assertEquals("Text around CDATA was lost", "xyz", text("<a>x<![CDATA[y]]>z</a>"));
    }

    @Test
    public void emptyCdata() {
        assertEquals("Empty CDATA was not ignored", "|", text("<a><![CDATA[]]>|</a>"));
    }

    @Test
    public void bracketsBeforeTerminator() {
        assertEquals("CDATA body was cut at the first ']'", "a]]b", text("<a><![CDATA[a]]b]]></a>"));
    }

    @Test
    public void whitespaceIsPreserved() {
        assertEquals("CDATA whitespace was trimmed", "  hi  ", text("<a><![CDATA[  hi  ]]></a>"));
    }

    @Test
    public void nestedElementHoldsCdata() {
        final var actual = parser.fromXml("<a><b><![CDATA[<c/>]]></b></a>");

        assertEquals("CDATA in a child element was lost", "<c/>",
            actual.findChildForName("b", null).getText());
    }

    @Test
    public void deserializeCdataIntoField() {
        final var actual = parser.fromXml("<pojo><name><![CDATA[<x>]]></name></pojo>", Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("CDATA was not deserialized into the field", "<x>", actual.name);
    }

    @Test(expected = InvalidXml.class)
    public void unterminatedCdata() {
        parser.fromXml("<a><![CDATA[abc</a>");
        fail("Unterminated CDATA section not detected");
    }

    @Test(expected = InvalidXml.class)
    public void cdataBeforeFirstTag() {
        parser.fromXml("<![CDATA[x]]><a/>");
        fail("CDATA before the opening tag not detected");
    }

    @Test
    public void compressPreservesCdata() {
        assertEquals("CDATA was not preserved when compressing",
            "<a><![CDATA[hi]]></a>", parser.compressXml("<a> <![CDATA[hi]]> </a>"));
    }

    @Test
    public void compressPreservesCdataContainingMarkup() {
        assertEquals("CDATA containing markup was rewritten",
            "<a><![CDATA[<b/>]]></a>", parser.compressXml("<a><![CDATA[<b/>]]></a>"));
    }

    @Test
    public void compressPreservesCdataContainingGreaterThan() {
        assertEquals("CDATA containing '>' was truncated",
            "<a><![CDATA[a > b]]></a>", parser.compressXml("<a><![CDATA[a > b]]></a>"));
    }

    private String text(final String xml) {
        return parser.fromXml(xml).getText();
    }

}
