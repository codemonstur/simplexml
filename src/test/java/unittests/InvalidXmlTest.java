package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;

import static org.junit.Assert.*;
import static xmlparser.XmlParser.newXmlParser;

public class InvalidXmlTest {

    private XmlParser parser = new XmlParser();
    private XmlParser lenient = newXmlParser().lenientWhitespace().build();

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
        final XmlElement element = lenient.fromXml("\ufeff \u205f \u180e <tag></tag>");

        assertNotNull(element);
        assertEquals("Name of tag is invalid", "tag", element.name);
    }

    //    @Test(expected = InvalidXml.class)
    public void nonWhitespaceAtEnd() {
        parser.fromXml("<tag></tag> blaa");
        fail("Non-whitespace characters after tags not caught");
    }

    //    @Test(expected = InvalidXml.class)
    public void unclosedTag() {
        parser.fromXml("<tag>");
        fail("Unclosed tag not caught");
    }

}
