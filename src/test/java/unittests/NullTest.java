package unittests;

import model.SimplePojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.*;
import static xmlparser.XmlParser.newXmlParser;

public class NullTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void serializeNulls() {
        final String pojoXml = "<simplepojo />";
        final SimplePojo pojo = new SimplePojo(null, null, null, null, null, null, null);

        final String xml = parser.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeNulls() {
        final String pojoXml = "<simplepojo><integer/></simplepojo>";

        final SimplePojo pojo = parser.fromXml(pojoXml, SimplePojo.class);

        assertNotNull("Pojo is null", pojo);
        assertNull("Boolean is not null", pojo.booleans);
        assertNull("Byte is not null", pojo.bytes);
        assertNull("Character is not null", pojo.character);
        assertNull("String is not null", pojo.string);
        assertNull("Short is not null", pojo.shorts);
        assertNull("Long is not null", pojo.longs);
        assertNull("Integer is not null", pojo.integer);
    }

}
