package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RemoveCommentsTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void simpleComment() {
        final var input = "<pojo><!-- comment --><name>Hello</name></pojo>";

        final var actualElement = parser.fromXml(input);
        final var actualPojo = parser.fromXml(input, Pojo.class);

        assertNotNull(actualElement, "XmlElement is null");

        final var name = actualElement.findChildForName("name", null);
        assertNotNull(name, "Name element is null");
        assertEquals("Hello", name.getText(), "Name element has the wrong name");
        assertNotNull(actualPojo, "Pojo is null");
        assertEquals("Hello", actualPojo.name, "Pojo has the wrong name");
    }

    @Test
    public void commentWithTag() {
        final var input = "<pojo><!-- <comment> --><name>Hello</name></pojo>";

        final var actualElement = parser.fromXml(input);
        final var actualPojo = parser.fromXml(input, Pojo.class);

        assertNotNull(actualElement, "XmlElement is null");

        final var name = actualElement.findChildForName("name", null);
        assertNotNull(name, "Name element is null");
        assertEquals("Hello", name.getText(), "Name element has the wrong name");
        assertNotNull(actualPojo, "Pojo is null");
        assertEquals("Hello", actualPojo.name, "Pojo has the wrong name");
    }

    @Test
    public void commentWithMoreTagStuff() {
        final var input = "<pojo><!-- <comment></comment> --< -- > <> --><name>Hello</name></pojo>";

        final var actualElement = parser.fromXml(input);
        final var actualPojo = parser.fromXml(input, Pojo.class);

        assertNotNull(actualElement, "XmlElement is null");

        final var name = actualElement.findChildForName("name", null);
        assertNotNull(name, "Name element is null");
        assertEquals("Hello", name.getText(), "Name element has the wrong name");
        assertNotNull(actualPojo, "Pojo is null");
        assertEquals("Hello", actualPojo.name, "Pojo has the wrong name");
    }

}
