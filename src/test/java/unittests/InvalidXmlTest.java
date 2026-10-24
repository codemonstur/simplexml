package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.jupiter.api.Assertions.*;
import static xmlparser.XmlParser.newXmlParser;

public class InvalidXmlTest {

    private final XmlParser parser = new XmlParser();
    private final XmlParser lenient = newXmlParser().lenientWhitespace().build();

    @Test
    public void nonWhitespaceAtStart() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("bla <tag></tag>"),
            "Non-whitespace characters before tags not caught");
    }

    @Test
    public void semiWhitespaceAtStart() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("\ufeff \u205f \u180e <tag></tag>"),
            "Non-whitespace characters before tags not caught");
    }

    @Test
    public void lenientWhitespaceAtStart() {
        final var actual = lenient.fromXml("\ufeff \u205f \u180e <tag></tag>");

        assertNotNull(actual);
        assertEquals("tag", actual.name, "Name of tag is invalid");
    }

    @Test
    public void attributeEqualsOnly() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag =></tag>"),
            "Bare equals character not detected");
    }

    @Test
    public void attributeNameWithoutEquals() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag word></tag>"),
            "Bare attribute without value not detected");
    }

    @Test
    public void nonWhitespaceAtEnd() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag></tag> blaa"),
            "Non-whitespace characters after tags not detected");
    }

    @Test
    public void noXmlAtAll() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml(" blaa bla val val"),
            "Failed to throw exception on no XML");
    }

    @Test
    public void unclosedTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag>"),
            "Unclosed tag not detected");
    }

    @Test
    public void unfinishedTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag  "),
            "Unfinished tag not detected");
    }

    @Test
    public void invalidCharacterInOpeningTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag> bla <2 </tag>"),
            "Invalid character in tag name not detected");
    }

    @Test
    public void extraClosingTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag></tag></tag>"),
            "Extra closing tag not detected");
    }

    @Test
    public void closingTagAfterSelfClosingTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag/></tag>"),
            "Closing tag of a self closing tag not detected");
    }

    @Test
    public void strayClosingTagBeforeOpeningTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("</tag><tag></tag>"),
            "Closing tag without an opening tag not detected");
    }

    @Test
    public void mismatchedClosingTag() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag><child>x</tag></child>"),
            "Mismatched closing tag not detected");
    }

    @Test
    public void mismatchedClosingTagReportsBothNames() {
        try {
            parser.fromXml("<tag></other>");
            fail("Mismatched closing tag not detected");
        } catch (final InvalidXml e) {
            assertEquals("Mismatched closing tag: expected </tag> but found </other>",
                    e.getMessage(), "Invalid error message");
        }
    }

    @Test
    public void secondRootElement() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag>1</tag><other>2</other>"),
            "A second root element not detected");
    }

    @Test
    public void thirdRootElement() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag>1</tag><other>2</other><third>3</third>"),
            "A third root element not detected");
    }

    @Test
    public void secondRootAfterNestedElement() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag><child>1</child></tag><other>2</other>"),
            "A second root element not detected");
    }

    @Test
    public void secondSelfClosingRootElement() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag/><other/>"),
            "A second self closing root element not detected");
    }

    @Test
    public void textBetweenTwoRoots() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag/>text<other/>"),
            "Text between two root elements not detected");
    }

    @Test
    public void secondRootAfterProlog() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<tag>1</tag><?pi?><other/>"),
            "A second root element not detected");
    }

    @Test
    public void secondRootElementReportsItsName() {
        try {
            parser.fromXml("<tag>1</tag><other>2</other>");
            fail("A second root element not detected");
        } catch (final InvalidXml e) {
            assertEquals("A second root element was found: <other> follows the end of the first",
                    e.getMessage(), "Invalid error message");
        }
    }

    @Test
    public void secondRootElementWhenDeserializing() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<pojo><name>a</name></pojo><pojo><name>b</name></pojo>", model.Pojo.class),
            "A second root element not detected");
    }

    @Test
    public void commentAfterRootElement() {
        assertEquals("tag", parser.fromXml("<tag>1</tag><!-- a comment -->").name,
                "A trailing comment was not allowed");
    }

    @Test
    public void tagNameContainingALessThan() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<a<b>x</a<b>"),
            "A tag name containing a '<' was not caught");
    }

    @Test
    public void tagNameContainingAnAmpersand() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<a&b>x</a&b>"),
            "A tag name containing a '&' was not caught");
    }

    @Test
    public void tagNameContainingAnEqualsSign() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<a=b>x</a=b>"),
            "A tag name containing an '=' was not caught");
    }

    @Test
    public void tagNameStartingWithADigit() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<1a>x</1a>"),
            "A tag name starting with a digit was not caught");
    }

    @Test
    public void invalidTagNameReportsTheWholeTag() {
        try {
            parser.fromXml("<a=b>x</a=b>");

            fail("A tag name containing an '=' was not caught");
        } catch (final InvalidXml e) {
            assertEquals("Found opening tag with invalid characters: a=b",
                    e.getMessage(), "Invalid error message");
        }
    }

    @Test
    public void tagNameThatIsNotAnAsciiWord() {
        assertEquals("my-tag", parser.fromXml("<my-tag>x</my-tag>").name, "A valid tag name was rejected");
        assertEquals("a.b", parser.fromXml("<a.b>x</a.b>").name, "A valid tag name was rejected");
        assertEquals("ns:elem", parser.fromXml("<ns:elem>x</ns:elem>").name, "A valid tag name was rejected");
        assertEquals("_x", parser.fromXml("<_x>x</_x>").name, "A valid tag name was rejected");
        assertEquals("é", parser.fromXml("<é>x</é>").name, "A valid tag name was rejected");
    }

}
