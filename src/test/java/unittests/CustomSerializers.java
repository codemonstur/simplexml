package unittests;

import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static simplexml.SimpleXml.newSimpleXml;

public class CustomSerializers {

    private static class Container {
        final String name;
        final Special special;
        private Container(final String name, final Special special) {
            this.name = name;
            this.special = special;
        }
    }

    private static class Special {
        final Integer id;
        final String name;
        private Special(final Integer id, final String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final Container CONTAINER = new Container("container", new Special(1, "special"));
    private static final String CONTAINER_XML = "<container>\n" +
            "  <name>container</name>\n" +
            "  <special>1:special</special>\n" +
            "</container>\n";

    private SimpleXml simple = newSimpleXml()
        .addDeserializer(Special.class, value -> {
            final String[] parts = value.split(":");
            if (parts.length != 2) return null;
            return new Special(Integer.parseInt(parts[0]), parts[1]);
        })
        .addSerializer(Special.class, value -> {
            final Special s = (Special)value;
            return s.id+":"+s.name;
        })
        .build();


    @Test
    public void serialize() throws IOException {
        final String xml = simple.toXml(CONTAINER);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", CONTAINER_XML, xml);
    }

    @Test
    public void deserialize() throws IOException {
        final Container c = simple.fromXml(CONTAINER_XML, Container.class);

        assertNotNull("Pojo is null", c);
        assertReflectionEquals(c, CONTAINER);
    }
}
