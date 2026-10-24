package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PrologTest {

    private final XmlParser parser = new XmlParser();

    private String text(final String xml) {
        return parser.fromXml(xml).getText();
    }

    @Test
    public void plainProlog() {
        assertEquals("x", text("<?xml version=\"1.0\"?><a>x</a>"), "Text was lost");
    }

    @Test
    public void greaterThanInsideAQuotedValue() {
        assertEquals("x", text("<?xml version=\"1.0\" encoding=\"x>y\"?><a>x</a>"),
                "The prolog was cut at the '>' inside its value");
    }

    @Test
    public void greaterThanInsideASingleQuotedValue() {
        assertEquals("x", text("<?xml version='1.0' encoding='x>y'?><a>x</a>"),
                "The prolog was cut at the '>' inside its value");
    }

    @Test
    public void greaterThanInsideAStylesheetValue() {
        assertEquals("x", text("<?xml-stylesheet type=\"text/xsl\" href=\"a>b.xsl\"?><a>x</a>"),
                "The instruction was cut at the '>' inside its value");
    }

    @Test
    public void greaterThanInsideInstructionData() {
        assertEquals("x", text("<?pi key=\"a>b\"?><a>x</a>"),
                "The instruction was cut at the '>' inside its value");
    }

    @Test
    public void apostropheInsideInstructionData() {
        assertEquals("x", text("<?pi it's data?><a>x</a>"),
                "An apostrophe in the data was read as the end of a value");
    }

    @Test
    public void severalPrologs() {
        assertEquals("x", text("<?xml version=\"1.0\" encoding=\"x>y\"?><?pi k=\"a>b\"?><a>x</a>"),
                "Text was lost");
    }

    @Test
    public void prologAfterAComment() {
        assertEquals("x", text("<!-- c --><?xml version=\"1.0\" encoding=\"x>y\"?><a>x</a>"), "Text was lost");
    }

    @Test
    public void prologEndingAtABareGreaterThanIsStillSkipped() {
        assertEquals("x", text("<?xml version=\"1.0\"><a>x</a>"),
                "A prolog without a '?>' was not skipped");
    }

    @Test
    public void unterminatedQuoteAtTheEndOfTheInput() {
        try {
            parser.fromXml("<?pi key=\"unterminated");

            fail("An unterminated quoted value was not reported");
        } catch (final InvalidXml e) {
            assertEquals("Unterminated quoted value before the end of the input", e.getMessage(),
                    "Invalid error message");
        }
    }

    @Test
    public void compressDropsAPrologWithAGreaterThanInItsValue() {
        assertEquals("<a>x</a>", parser.compressXml("<?xml version=\"1.0\" encoding=\"x>y\"?><a>x</a>"),
                "A prolog was not dropped");
    }

    @Test
    public void compressKeepsAProcessingInstructionWithAGreaterThanInItsValue() {
        assertEquals("<a>x</a>", parser.compressXml("<?pi key=\"a>b\"?><a>x</a>"),
                "A processing instruction was not dropped");
    }

    @Test
    public void commentWithAGreaterThanAndAnApostrophe() {
        assertEquals("x", text("<!-- don't > stop --><a>x</a>"),
                "A comment was cut short");
    }

    @Test
    public void cdataWithAGreaterThanAndAnApostrophe() {
        assertEquals("it's a > b", text("<a><![CDATA[it's a > b]]></a>"),
                "CDATA was cut short");
    }

    @Test
    public void greaterThanInsideAnAttributeValue() {
        assertEquals("z", text("<a key=\"x>y\">z</a>"),
                "An attribute value was cut short");
        assertEquals("z", text("<a key='x>y'>z</a>"),
                "An attribute value was cut short");
    }

}
