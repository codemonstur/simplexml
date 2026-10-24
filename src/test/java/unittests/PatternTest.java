package unittests;

import model.PatternNumberPojo;
import model.PatternPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.jupiter.api.Assertions.*;

public class PatternTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializePattern() {
        final var input = "<pojo field=\"field\">Hello</pojo>";

        final var actual = parser.fromXml(input, PatternPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("field", actual.field, "Pojo has the wrong attribute");
        assertEquals("Hello", actual.text, "Pojo has the text node");
    }

    @Test
    public void deserializePatternAttributeInvalid() {
        final var input = "<pojo field=\"1234\">Hello</pojo>";

        assertThrows(InvalidXml.class, () -> parser.fromXml(input, PatternPojo.class));
    }

    @Test
    public void deserializePatternTextInvalid() {
        final var input = "<pojo field=\"field\">1234</pojo>";

        assertThrows(InvalidXml.class, () -> parser.fromXml(input, PatternPojo.class));
    }

    @Test
    public void deserializeIntegerPattern() {
        final var input = "<pojo field=\"1234\">1234</pojo>";

        final var actual = parser.fromXml(input, PatternNumberPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(Integer.valueOf(1234), actual.field, "Pojo has the wrong attribute");
        assertEquals(1234, actual.text, "Pojo has the text node");
    }

    @Test
    public void deserializeInvalidAttributeIntegerPattern() {
        final var input = "<pojo field=\"1\">1234</pojo>";

        assertThrows(InvalidXml.class, () -> parser.fromXml(input, PatternNumberPojo.class));
    }

    @Test
    public void deserializeInvalidTextIntegerPattern() {
        final var input = "<pojo field=\"1234\">1</pojo>";

        assertThrows(InvalidXml.class, () -> parser.fromXml(input, PatternNumberPojo.class));
    }

}
