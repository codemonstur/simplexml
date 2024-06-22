package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.*;
import static xmlparser.model.XmlElement.newElement;

public class DomTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void selfClosingParameter() {
        final var input = "<pojo><name>Hello</name><closing /></pojo>";

        final var actual = parser.fromXml(input);

        assertNotNull("Pojo is null", actual);
        assertFalse("<pojo> tag is self closing", actual.isSelfClosing());
        assertFalse("<name> tag is self closing", actual.children.get(0).isSelfClosing());
        assertTrue("<closing> tag is self closing", actual.children.get(1).isSelfClosing());
    }

    @Test
    public void testGetElementsByTagName() {
        final var input = "<pojo><name>Hello</name></pojo>";
        final var actual = parser.fromXml(input).getElementsByTagName("name");

        assertNotNull("Pojo is null", actual);
        assertEquals("List size is incorrect", 1, actual.size());
        assertEquals("List item is incorrect", "name", actual.get(0).name);
    }

    @Test
    public void testToStringElement() {
        final var input = "<pojo><name>Hello</name></pojo>";
        final var actual = parser.fromXml(input);

        assertNotNull("Missing dom object", actual);
        assertEquals("String output is incorrect", "XmlElement[pojo]", actual.toString());
        assertEquals("String output is incorrect", "XmlElement[name]", actual.children.get(0).toString());
    }

    @Test
    public void testToStringTextNode() {
        final var actual = newElement("pojo").text("hello").build();

        assertEquals("String output is incorrect", "XmlElement[pojo]", actual.toString());
        assertEquals("String output is incorrect", "XmlTextElement[hello]", actual.children.get(0).toString());
    }

    @Test
    public void testPrettyPrinting() {
        final var input = newElement("pojo")
                .child(newElement("child"))
                .text("hello").build();
        final var expected = "<pojo>\n  <child />\n  hello\n</pojo>\n";

        final var actual = parser.domToXml(input);

        assertNotNull(actual);
        assertEquals("Invalid XML output", expected, actual);
    }

}
