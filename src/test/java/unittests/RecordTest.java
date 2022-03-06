package unittests;

import model.RecordPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecordTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void serializeRecord() {
        final String pojoXml = "<recordpojo>\n  <name>Hello</name>\n</recordpojo>\n";
        final RecordPojo pojo = new RecordPojo("Hello");

        final String xml = parser.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeRecord() {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final RecordPojo pojo = parser.fromXml(pojoXml, RecordPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name());
    }

}
