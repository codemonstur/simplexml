import model.Pojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleTest {

    private SimpleXml simple = new SimpleXml();

    @Test
    public void deserialize() throws IOException {
        final String pojoXml = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final Pojo pojo = simple.fromXml(pojoXml, Pojo.class);

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

}
