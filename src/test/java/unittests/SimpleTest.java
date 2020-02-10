package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;
import static xmlparser.model.XmlElement.newElement;

public class SimpleTest {

    private XmlParser parser = new XmlParser();
    private XmlParser parserShort = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void deserializeObject() {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void deserializeShort() {
        final String pojoXml = "<pojo><name>Hello</name></pojo>";

        final Pojo pojo = parserShort.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void serialize() {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("Hello");

        final String xml = parser.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void serializeDom() {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";
        final XmlElement dom = newElement("pojo").child(newElement("name").text("Hello")).build();

        final String xml = parser.domToXml(dom);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void serializeShort() {
        final String pojoXml = "<pojo><name>Hello</name></pojo>";
        final Pojo pojo = new Pojo("Hello");

        final String xml = parserShort.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void serializeDomShort() {
        final String pojoXml = "<pojo><name>Hello</name></pojo>";
        final XmlElement dom = newElement("pojo").child(newElement("name").text("Hello")).build();

        final String xml = parserShort.domToXml(dom);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }
}
