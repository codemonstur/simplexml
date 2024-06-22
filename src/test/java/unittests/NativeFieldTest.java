package unittests;

import model.NativePojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class NativeFieldTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void deserializeInt() {
        final var input = "<nativepojo><id>5</id></nativepojo>";

        final var actual = parser.fromXml(input, NativePojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong id", 5, actual.id);
    }

    @Test
    public void serializeInt() {
        final var input = new NativePojo(5);
        final var expected = "<nativepojo><id>5</id></nativepojo>";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

}
