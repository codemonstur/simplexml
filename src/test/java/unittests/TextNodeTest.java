package unittests;

import model.PojoWithText;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class TextNodeTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void textNodeIsDeserialized() {
        final var input = "<pojowithtext><name>Hello</name>World</pojowithtext>";

        final var actual = parser.fromXml(input, PojoWithText.class);

        assertNotNull(actual, "No deserialization response");
        assertEquals("Hello", actual.name, "Invalid deserialized output");
        assertEquals("World", actual.text, "Invalid deserialized output");
    }

    @Test
    public void textNodeIsSerialized() {
        final var input = new PojoWithText("Hello", "World");
        final var expected = "<pojowithtext><name>Hello</name>World</pojowithtext>";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

}
