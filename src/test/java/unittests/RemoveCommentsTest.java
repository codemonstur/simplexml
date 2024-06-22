package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RemoveCommentsTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void simpleComment() {
        final var input = "<pojo><!-- comment --><name>Hello</name></pojo>";

        final var actualElement = parser.fromXml(input);
        final var actualPojo = parser.fromXml(input, Pojo.class);

        assertNotNull("XmlElement is null", actualElement);

        final var name = actualElement.findChildForName("name", null);
        assertNotNull("Name element is null", name);
        assertEquals("Name element has the wrong name", "Hello", name.getText());
        assertNotNull("Pojo is null", actualPojo);
        assertEquals("Pojo has the wrong name", "Hello", actualPojo.name);
    }

    @Test
    public void commentWithTag() {
        final var input = "<pojo><!-- <comment> --><name>Hello</name></pojo>";

        final var actualElement = parser.fromXml(input);
        final var actualPojo = parser.fromXml(input, Pojo.class);

        assertNotNull("XmlElement is null", actualElement);

        final var name = actualElement.findChildForName("name", null);
        assertNotNull("Name element is null", name);
        assertEquals("Name element has the wrong name", "Hello", name.getText());
        assertNotNull("Pojo is null", actualPojo);
        assertEquals("Pojo has the wrong name", "Hello", actualPojo.name);
    }

    @Test
    public void commentWithMoreTagStuff() {
        final var input = "<pojo><!-- <comment></comment> --< -- > <> --><name>Hello</name></pojo>";

        final var actualElement = parser.fromXml(input);
        final var actualPojo = parser.fromXml(input, Pojo.class);

        assertNotNull("XmlElement is null", actualElement);

        final var name = actualElement.findChildForName("name", null);
        assertNotNull("Name element is null", name);
        assertEquals("Name element has the wrong name", "Hello", name.getText());
        assertNotNull("Pojo is null", actualPojo);
        assertEquals("Pojo has the wrong name", "Hello", actualPojo.name);
    }

}
