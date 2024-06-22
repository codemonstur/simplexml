package unittests;

import model.PatternNumberPojo;
import model.PatternPojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.Assert.*;

public class PatternTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializePattern() {
        final var input = "<pojo field=\"field\">Hello</pojo>";

        final var actual = parser.fromXml(input, PatternPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong attribute", "field", actual.field);
        assertEquals("Pojo has the text node", "Hello", actual.text);
    }

    @Test(expected = InvalidXml.class)
    public void deserializePatternAttributeInvalid() {
        final var input = "<pojo field=\"1234\">Hello</pojo>";

        parser.fromXml(input, PatternPojo.class);

        fail("Exception wasn't thrown");
    }

    @Test(expected = InvalidXml.class)
    public void deserializePatternTextInvalid() {
        final var input = "<pojo field=\"field\">1234</pojo>";

        parser.fromXml(input, PatternPojo.class);

        fail("Exception wasn't thrown");
    }

    @Test
    public void deserializeIntegerPattern() {
        final var input = "<pojo field=\"1234\">1234</pojo>";

        final var actual = parser.fromXml(input, PatternNumberPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong attribute", Integer.valueOf(1234), actual.field);
        assertEquals("Pojo has the text node", 1234, actual.text);
    }

    @Test(expected = InvalidXml.class)
    public void deserializeInvalidAttributeIntegerPattern() {
        final var input = "<pojo field=\"1\">1234</pojo>";

        parser.fromXml(input, PatternNumberPojo.class);

        fail("Exception not thrown");
    }

    @Test(expected = InvalidXml.class)
    public void deserializeInvalidTextIntegerPattern() {
        final var input = "<pojo field=\"1234\">1</pojo>";

        parser.fromXml(input, PatternNumberPojo.class);

        fail("Exception not thrown");
    }

}
