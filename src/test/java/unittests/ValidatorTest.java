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
        final var input = "<testablepojo>\n  <name>Hello</name>\n</testablepojo>\n";

        final var actual = parser.fromXml(input, ValidatorPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalid() {
        final var input = "<testablepojo>\n  <name>invalid</name>\n</testablepojo>\n";

        parser.fromXml(input, ValidatorPojo.class);

        fail("Invalid object was created");
    }

}
