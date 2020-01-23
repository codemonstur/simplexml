package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RemoveCommentsTest {

    private XmlParser parser = new XmlParser();

    @Test
    public void simpleComment() {
        final String pojoXml = "<pojo><!-- comment --><name>Hello</name></pojo>";

        final XmlElement xmlElement = parser.fromXml(pojoXml);
        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("XmlElement is null", xmlElement);

        final XmlElement name = xmlElement.findChildForName("name", null);
        assertNotNull("Name element is null", name);
        assertEquals("Name element has the wrong name", "Hello", name.getText());
        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void commentWithTag() {
        final String pojoXml = "<pojo><!-- <comment> --><name>Hello</name></pojo>";

        final XmlElement xmlElement = parser.fromXml(pojoXml);
        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("XmlElement is null", xmlElement);

        final XmlElement name = xmlElement.findChildForName("name", null);
        assertNotNull("Name element is null", name);
        assertEquals("Name element has the wrong name", "Hello", name.getText());
        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void commentWithMoreTagStuff() {
        final String pojoXml = "<pojo><!-- <comment></comment> --< -- > <> --><name>Hello</name></pojo>";

        final XmlElement xmlElement = parser.fromXml(pojoXml);
        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("XmlElement is null", xmlElement);

        final XmlElement name = xmlElement.findChildForName("name", null);
        assertNotNull("Name element is null", name);
        assertEquals("Name element has the wrong name", "Hello", name.getText());
        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

}
