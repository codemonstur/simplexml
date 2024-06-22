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
        final var input = new SimplePojo(null, null, null, null, null, null, null);
        final var expected = "<simplepojo />";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeNulls() {
        final var input = "<simplepojo><integer/></simplepojo>";

        final var actual = parser.fromXml(input, SimplePojo.class);

        assertNotNull("Pojo is null", actual);
        assertNull("Boolean is not null", actual.booleans);
        assertNull("Byte is not null", actual.bytes);
        assertNull("Character is not null", actual.character);
        assertNull("String is not null", actual.string);
        assertNull("Short is not null", actual.shorts);
        assertNull("Long is not null", actual.longs);
        assertNull("Integer is not null", actual.integer);
    }

}
