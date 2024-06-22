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
        final var input = new RecordPojo("Hello");
        final var expected = "<recordpojo>\n  <name>Hello</name>\n</recordpojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeRecord() {
        final var input = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final var actual = parser.fromXml(input, RecordPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name());
    }

}
