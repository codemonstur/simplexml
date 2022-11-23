package unittests;

import model.ValidatorPojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidObject;

import static org.junit.Assert.*;

public class ValidatorTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeValid() {
        final String pojoXml = "<testablepojo>\n  <name>Hello</name>\n</testablepojo>\n";

        final ValidatorPojo pojo = parser.fromXml(pojoXml, ValidatorPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalid() {
        final String pojoXml = "<testablepojo>\n  <name>invalid</name>\n</testablepojo>\n";

        parser.fromXml(pojoXml, ValidatorPojo.class);

        fail("Invalid object was created");
    }

}
