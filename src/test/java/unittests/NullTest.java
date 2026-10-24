package unittests;

import model.SimplePojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.*;
import static xmlparser.XmlParser.newXmlParser;

public class NullTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void serializeNulls() {
        final var input = new SimplePojo(null, null, null, null, null, null, null);
        final var expected = "<simplepojo />";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void deserializeNulls() {
        final var input = "<simplepojo><integer/></simplepojo>";

        final var actual = parser.fromXml(input, SimplePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertNull(actual.booleans, "Boolean is not null");
        assertNull(actual.bytes, "Byte is not null");
        assertNull(actual.character, "Character is not null");
        assertNull(actual.string, "String is not null");
        assertNull(actual.shorts, "Short is not null");
        assertNull(actual.longs, "Long is not null");
        assertNull(actual.integer, "Integer is not null");
    }

}
