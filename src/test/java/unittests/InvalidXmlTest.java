package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.Assert.*;
import static xmlparser.XmlParser.newXmlParser;

public class InvalidXmlTest {

    private final XmlParser parser = new XmlParser();
    private final XmlParser lenient = newXmlParser().lenientWhitespace().build();

    @Test(expected = InvalidXml.class)
    public void nonWhitespaceAtStart() {
        parser.fromXml("bla <tag></tag>");
        fail("Non-whitespace characters before tags not caught");
    }

    @Test(expected = InvalidXml.class)
    public void semiWhitespaceAtStart() {
        parser.fromXml("\ufeff \u205f \u180e <tag></tag>");
        fail("Non-whitespace characters before tags not caught");
    }

    @Test
    public void lenientWhitespaceAtStart() {
        final var actual = lenient.fromXml("\ufeff \u205f \u180e <tag></tag>");

        assertNotNull(actual);
        assertEquals("Name of tag is invalid", "tag", actual.name);
    }

    @Test(expected = InvalidXml.class)
    public void attributeEqualsOnly() {
        parser.fromXml("<tag =></tag>");
        fail("Bare equals character not detected");
    }

    @Test(expected = InvalidXml.class)
    public void attributeNameWithoutEquals() {
        parser.fromXml("<tag word></tag>");
        fail("Bare attribute without value not detected");
    }

    @Test(expected = InvalidXml.class)
    public void nonWhitespaceAtEnd() {
        parser.fromXml("<tag></tag> blaa");
        fail("Non-whitespace characters after tags not detected");
    }

    @Test(expected = InvalidXml.class)
    public void noXmlAtAll() {
        parser.fromXml(" blaa bla val val");
        fail("Failed to throw exception on no XML");
    }

    @Test(expected = InvalidXml.class)
    public void unclosedTag() {
        parser.fromXml("<tag>");
        fail("Unclosed tag not detected");
    }

    @Test(expected = InvalidXml.class)
    public void unfinishedTag() {
        parser.fromXml("<tag  ");
        fail("Unfinished tag not detected");
    }

    @Test(expected = InvalidXml.class)
    public void invalidCharacterInOpeningTag() {
        parser.fromXml("<tag> bla <2 </tag>");
        fail("Invalid character in tag name not detected");
    }

    @Test(expected = InvalidXml.class)
    public void extraClosingTag() {
        parser.fromXml("<tag></tag></tag>");
        fail("Extra closing tag not detected");
    }

    @Test(expected = InvalidXml.class)
    public void closingTagAfterSelfClosingTag() {
        parser.fromXml("<tag/></tag>");
        fail("Closing tag of a self closing tag not detected");
    }

    @Test(expected = InvalidXml.class)
    public void strayClosingTagBeforeOpeningTag() {
        parser.fromXml("</tag><tag></tag>");
        fail("Closing tag without an opening tag not detected");
    }

    @Test(expected = InvalidXml.class)
    public void mismatchedClosingTag() {
        parser.fromXml("<tag><child>x</tag></child>");
        fail("Mismatched closing tag not detected");
    }

    @Test
    public void mismatchedClosingTagReportsBothNames() {
        try {
            parser.fromXml("<tag></other>");
            fail("Mismatched closing tag not detected");
        } catch (final InvalidXml e) {
            assertEquals("Invalid error message",
                    "Mismatched closing tag: expected </tag> but found </other>", e.getMessage());
        }
    }

    @Test(expected = InvalidXml.class)
    public void secondRootElement() {
        parser.fromXml("<tag>1</tag><other>2</other>");
        fail("A second root element not detected");
    }

    @Test(expected = InvalidXml.class)
    public void thirdRootElement() {
        parser.fromXml("<tag>1</tag><other>2</other><third>3</third>");
        fail("A third root element not detected");
    }

    @Test(expected = InvalidXml.class)
    public void secondRootAfterNestedElement() {
        parser.fromXml("<tag><child>1</child></tag><other>2</other>");
        fail("A second root element not detected");
    }

    @Test(expected = InvalidXml.class)
    public void secondSelfClosingRootElement() {
        parser.fromXml("<tag/><other/>");
        fail("A second self closing root element not detected");
    }

    @Test(expected = InvalidXml.class)
    public void textBetweenTwoRoots() {
        parser.fromXml("<tag/>text<other/>");
        fail("Text between two root elements not detected");
    }

    @Test(expected = InvalidXml.class)
    public void secondRootAfterProlog() {
        parser.fromXml("<tag>1</tag><?pi?><other/>");
        fail("A second root element not detected");
    }

    @Test
    public void secondRootElementReportsItsName() {
        try {
            parser.fromXml("<tag>1</tag><other>2</other>");
            fail("A second root element not detected");
        } catch (final InvalidXml e) {
            assertEquals("Invalid error message",
                    "A second root element was found: <other> follows the end of the first", e.getMessage());
        }
    }

    @Test(expected = InvalidXml.class)
    public void secondRootElementWhenDeserializing() {
        parser.fromXml("<pojo><name>a</name></pojo><pojo><name>b</name></pojo>", model.Pojo.class);
        fail("A second root element not detected");
    }

    @Test
    public void commentAfterRootElement() {
        assertEquals("A trailing comment was not allowed", "tag",
                parser.fromXml("<tag>1</tag><!-- a comment -->").name);
    }

    @Test(expected = InvalidXml.class)
    public void tagNameContainingALessThan() {
        parser.fromXml("<a<b>x</a<b>");
        fail("A tag name containing a '<' was not caught");
    }

    @Test(expected = InvalidXml.class)
    public void tagNameContainingAnAmpersand() {
        parser.fromXml("<a&b>x</a&b>");
        fail("A tag name containing a '&' was not caught");
    }

    @Test(expected = InvalidXml.class)
    public void tagNameContainingAnEqualsSign() {
        parser.fromXml("<a=b>x</a=b>");
        fail("A tag name containing an '=' was not caught");
    }

    @Test(expected = InvalidXml.class)
    public void tagNameStartingWithADigit() {
        parser.fromXml("<1a>x</1a>");
        fail("A tag name starting with a digit was not caught");
    }

    @Test
    public void invalidTagNameReportsTheWholeTag() {
        try {
            parser.fromXml("<a=b>x</a=b>");

            fail("A tag name containing an '=' was not caught");
        } catch (final InvalidXml e) {
            assertEquals("Invalid error message",
                    "Found opening tag with invalid characters: a=b", e.getMessage());
        }
    }

    @Test
    public void tagNameThatIsNotAnAsciiWord() {
        assertEquals("A valid tag name was rejected", "my-tag", parser.fromXml("<my-tag>x</my-tag>").name);
        assertEquals("A valid tag name was rejected", "a.b", parser.fromXml("<a.b>x</a.b>").name);
        assertEquals("A valid tag name was rejected", "ns:elem", parser.fromXml("<ns:elem>x</ns:elem>").name);
        assertEquals("A valid tag name was rejected", "_x", parser.fromXml("<_x>x</_x>").name);
        assertEquals("A valid tag name was rejected", "é", parser.fromXml("<é>x</é>").name);
    }

}
