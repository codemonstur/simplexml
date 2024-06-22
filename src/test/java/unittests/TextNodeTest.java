package unittests;

import model.PojoWithText;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class TextNodeTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void textNodeIsDeserialized() {
        final var input = "<pojowithtext><name>Hello</name>World</pojowithtext>";

        final var actual = parser.fromXml(input, PojoWithText.class);

        assertNotNull("No deserialization response", actual);
        assertEquals("Invalid deserialized output", "Hello", actual.name);
        assertEquals("Invalid deserialized output", "World", actual.text);
    }

    @Test
    public void textNodeIsSerialized() {
        final var input = new PojoWithText("Hello", "World");
        final var expected = "<pojowithtext><name>Hello</name>World</pojowithtext>";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

}
