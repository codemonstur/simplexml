package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;

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
        final var actual = parser.fromXml("<tag =></tag>");
        fail("Bare equals character not detected");
    }

    @Test(expected = InvalidXml.class)
    public void attributeNameWithoutEquals() {
        final var actual = parser.fromXml("<tag word></tag>");
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

}
