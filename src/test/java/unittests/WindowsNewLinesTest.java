package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class WindowsNewLinesTest {

    private final XmlParser parser = newXmlParser().windowsNewLines().build();

    @Test
    public void serializeClass() {
        final var input = new Pojo("Hello");
        final var expected = "<pojo>\r\n  <name>Hello</name>\r\n</pojo>\r\n";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

}
