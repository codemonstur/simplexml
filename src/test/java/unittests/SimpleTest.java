package unittests;

import model.Pojo;
import org.junit.Test;
import simplexml.SimpleXml;
import simplexml.model.Element;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static simplexml.SimpleXml.newSimpleXml;
import static simplexml.model.Element.element;

public class SimpleTest {

    private SimpleXml simple = new SimpleXml();
    private SimpleXml simpler = newSimpleXml().shouldPrettyPrint(false).build();

    @Test
    public void deserializeObject() throws IOException {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final Pojo pojo = simple.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void deserializeShort() throws IOException {
        final String pojoXml = "<pojo><name>Hello</name></pojo>";

        final Pojo pojo = simpler.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void serialize() {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("Hello");

        final String xml = simple.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void serializeDom() {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";
        final Element dom = element("pojo").child(element("name").text("Hello"));

        final String xml = simple.domToXml(dom);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void serializeShort() {
        final String pojoXml = "<pojo><name>Hello</name></pojo>";
        final Pojo pojo = new Pojo("Hello");

        final String xml = simpler.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void serializeDomShort() {
        final String pojoXml = "<pojo><name>Hello</name></pojo>";
        final Element dom = element("pojo").child(element("name").text("Hello"));

        final String xml = simpler.domToXml(dom);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }
}
