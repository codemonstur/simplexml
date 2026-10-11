package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PrologTest {

    private final XmlParser parser = new XmlParser();

    private String text(final String xml) {
        return parser.fromXml(xml).getText();
    }

    @Test
    public void plainProlog() {
        assertEquals("Text was lost", "x", text("<?xml version=\"1.0\"?><a>x</a>"));
    }

    @Test
    public void greaterThanInsideAQuotedValue() {
        assertEquals("The prolog was cut at the '>' inside its value", "x",
                text("<?xml version=\"1.0\" encoding=\"x>y\"?><a>x</a>"));
    }

    @Test
    public void greaterThanInsideASingleQuotedValue() {
        assertEquals("The prolog was cut at the '>' inside its value", "x",
                text("<?xml version='1.0' encoding='x>y'?><a>x</a>"));
    }

    @Test
    public void greaterThanInsideAStylesheetValue() {
        assertEquals("The instruction was cut at the '>' inside its value", "x",
                text("<?xml-stylesheet type=\"text/xsl\" href=\"a>b.xsl\"?><a>x</a>"));
    }

    @Test
    public void greaterThanInsideInstructionData() {
        assertEquals("The instruction was cut at the '>' inside its value", "x",
                text("<?pi key=\"a>b\"?><a>x</a>"));
    }

    @Test
    public void apostropheInsideInstructionData() {
        assertEquals("An apostrophe in the data was read as the end of a value", "x",
                text("<?pi it's data?><a>x</a>"));
    }

    @Test
    public void severalPrologs() {
        assertEquals("Text was lost", "x",
                text("<?xml version=\"1.0\" encoding=\"x>y\"?><?pi k=\"a>b\"?><a>x</a>"));
    }

    @Test
    public void prologAfterAComment() {
        assertEquals("Text was lost", "x",
                text("<!-- c --><?xml version=\"1.0\" encoding=\"x>y\"?><a>x</a>"));
    }

    @Test
    public void prologEndingAtABareGreaterThanIsStillSkipped() {
        assertEquals("A prolog without a '?>' was not skipped", "x",
                text("<?xml version=\"1.0\"><a>x</a>"));
    }

    @Test
    public void unterminatedQuoteAtTheEndOfTheInput() {
        try {
            parser.fromXml("<?pi key=\"unterminated");

            fail("An unterminated quoted value was not reported");
        } catch (final InvalidXml e) {
            assertEquals("Invalid error message",
                    "Unterminated quoted value before the end of the input", e.getMessage());
        }
    }

    @Test
    public void compressDropsAPrologWithAGreaterThanInItsValue() {
        assertEquals("A prolog was not dropped", "<a>x</a>",
                parser.compressXml("<?xml version=\"1.0\" encoding=\"x>y\"?><a>x</a>"));
    }

    @Test
    public void compressKeepsAProcessingInstructionWithAGreaterThanInItsValue() {
        assertEquals("A processing instruction was not dropped", "<a>x</a>",
                parser.compressXml("<?pi key=\"a>b\"?><a>x</a>"));
    }

    @Test
    public void commentWithAGreaterThanAndAnApostrophe() {
        assertEquals("A comment was cut short", "x", text("<!-- don't > stop --><a>x</a>"));
    }

    @Test
    public void cdataWithAGreaterThanAndAnApostrophe() {
        assertEquals("CDATA was cut short", "it's a > b", text("<a><![CDATA[it's a > b]]></a>"));
    }

    @Test
    public void greaterThanInsideAnAttributeValue() {
        assertEquals("An attribute value was cut short", "z", text("<a key=\"x>y\">z</a>"));
        assertEquals("An attribute value was cut short", "z", text("<a key='x>y'>z</a>"));
    }

}
