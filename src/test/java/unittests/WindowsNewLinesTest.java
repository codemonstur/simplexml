package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class WindowsNewLinesTest {

    private final XmlParser parser = newXmlParser().windowsNewLines().build();

    @Test
    public void serializeClass() {
        final String pojoXml = "<pojo>\r\n  <name>Hello</name>\r\n</pojo>\r\n";
        final Pojo pojo = new Pojo("Hello");

        final String xml = parser.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

}
