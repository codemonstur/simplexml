package unittests;

import model.NativePojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class NativeFieldTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void deserializeInt() {
        final String pojoXml = "<nativepojo><id>5</id></nativepojo>";

        final NativePojo pojo = parser.fromXml(pojoXml, NativePojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong id", 5, pojo.id);
    }

    @Test
    public void serializeInt() {
        final String pojoXml = "<nativepojo><id>5</id></nativepojo>";
        final NativePojo pojo = new NativePojo(5);

        final String xml = parser.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

}
