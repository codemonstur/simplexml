package unittests;

import model.SimplePojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static simplexml.SimpleXml.newSimpleXml;

public class NullTest {

    private SimpleXml simple = newSimpleXml().shouldPrettyPrint(false).build();

    @Test
    public void serializeNulls() {
        final String pojoXml = "<simplepojo />";
        final SimplePojo pojo = new SimplePojo(null, null, null, null, null, null, null);

        final String xml = simple.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeNulls() throws IOException {
        final String pojoXml = "<simplepojo><integers /></simplepojo>";

        final SimplePojo pojo = simple.fromXml(pojoXml, SimplePojo.class);

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
