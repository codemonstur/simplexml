package unittests;

import model.TestablePojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidObject;

import static org.junit.Assert.*;

public class ValidatorTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeValid() {
        final String pojoXml = "<testablepojo>\n  <name>Hello</name>\n</testablepojo>\n";

        final TestablePojo pojo = parser.fromXml(pojoXml, TestablePojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalid() {
        final String pojoXml = "<testablepojo>\n  <name>invalid</name>\n</testablepojo>\n";

        parser.fromXml(pojoXml, TestablePojo.class);

        fail("Invalid object was created");
    }

}
