package unittests;

import model.NativePojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class NativeFieldTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void deserializeInt() {
        final var input = "<nativepojo><id>5</id></nativepojo>";

        final var actual = parser.fromXml(input, NativePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(5, actual.id, "Pojo has the wrong id");
    }

    @Test
    public void serializeInt() {
        final var input = new NativePojo(5);
        final var expected = "<nativepojo><id>5</id></nativepojo>";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void deserializeMissingInt() {
        final var input = "<nativepojo/>";

        final var actual = parser.fromXml(input, NativePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(0, actual.id, "Missing int should have its zero value");
    }

}
