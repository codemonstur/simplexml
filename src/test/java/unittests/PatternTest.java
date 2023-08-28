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
        final String pojoXml = "<pojo field=\"field\">Hello</pojo>";

        final PatternPojo pojo = parser.fromXml(pojoXml, PatternPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong attribute", "field", pojo.field);
        assertEquals("Pojo has the text node", "Hello", pojo.text);
    }

    @Test(expected = InvalidXml.class)
    public void deserializePatternAttributeInvalid() {
        final String pojoXml = "<pojo field=\"1234\">Hello</pojo>";
        parser.fromXml(pojoXml, PatternPojo.class);

        fail("Exception wasn't thrown");
    }

    @Test(expected = InvalidXml.class)
    public void deserializePatternTextInvalid() {
        final String pojoXml = "<pojo field=\"field\">1234</pojo>";
        parser.fromXml(pojoXml, PatternPojo.class);

        fail("Exception wasn't thrown");
    }

    @Test
    public void deserializeIntegerPattern() {
        final String pojoXml = "<pojo field=\"1234\">1234</pojo>";

        final PatternNumberPojo pojo = parser.fromXml(pojoXml, PatternNumberPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong attribute", Integer.valueOf(1234), pojo.field);
        assertEquals("Pojo has the text node", 1234, pojo.text);
    }

    @Test(expected = InvalidXml.class)
    public void deserializeInvalidAttributeIntegerPattern() {
        final String pojoXml = "<pojo field=\"1\">1234</pojo>";
        final PatternNumberPojo pojo = parser.fromXml(pojoXml, PatternNumberPojo.class);

        fail("Exception not thrown");
    }

    @Test(expected = InvalidXml.class)
    public void deserializeInvalidTextIntegerPattern() {
        final String pojoXml = "<pojo field=\"1234\">1</pojo>";
        final PatternNumberPojo pojo = parser.fromXml(pojoXml, PatternNumberPojo.class);

        fail("Exception not thrown");
    }

}
