package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;
import static xmlparser.model.XmlElement.newElement;

public class SimpleTest {

    private final XmlParser parser = new XmlParser();
    private final XmlParser parserShort = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void deserializeClass() {
        final var input = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void deserializeShort() {
        final var input = "<pojo><name>Hello</name></pojo>";

        final var actual = parserShort.fromXml(input, Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void serializeClass() {
        final var input = new Pojo("Hello");
        final var expected = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void serializeDom() {
        final var input = newElement("pojo").child(newElement("name").text("Hello")).build();
        final var expected = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final var actual = parser.domToXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void serializeShort() {
        final var input = new Pojo("Hello");
        final var expected = "<pojo><name>Hello</name></pojo>";

        final var actual = parserShort.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void serializeDomShort() {
        final var input = newElement("pojo").child(newElement("name").text("Hello")).build();
        final var expected = "<pojo><name>Hello</name></pojo>";

        final var actual = parserShort.domToXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

}
