package unittests;

import model.NativePojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static simplexml.SimpleXml.newSimpleXml;

public class NativeFieldTest {

    private SimpleXml simple = newSimpleXml().shouldPrettyPrint(false).build();

    @Test
    public void deserializeInt() throws IOException {
        final String pojoXml = "<nativepojo><id>5</id></nativepojo>";

        final NativePojo pojo = simple.fromXml(pojoXml, NativePojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong id", 5, pojo.id);
    }

    @Test
    public void serializeInt() {
        final String pojoXml = "<nativepojo><id>5</id></nativepojo>";
        final NativePojo pojo = new NativePojo(5);

        final String xml = simple.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

}
