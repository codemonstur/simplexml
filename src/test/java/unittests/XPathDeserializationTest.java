package unittests;

import model.XPathPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XPathDeserializationTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeObject() {
        final String xml = "<xpathpojo><wrapper><object>Hello</object></wrapper></xpathpojo>";

        final XPathPojo pojo = parser.fromXml(xml, XPathPojo.class);

        assertNotNull("POJO not deserialized", pojo);
        assertNotNull("POJO value not deserialized", pojo.object);
        assertEquals("POJO values not equal", "Hello", pojo.object);
    }

    @Test
    public void deserializeAttribute() {
        final String xml = "<xpathpojo><wrapper attr=\"hello\" /></xpathpojo>";

        final XPathPojo pojo = parser.fromXml(xml, XPathPojo.class);

        assertNotNull("POJO not deserialized", pojo);
        assertNotNull("POJO value not deserialized", pojo.attr);
        assertEquals("POJO values not equal", "hello", pojo.attr);
    }

    @Test
    public void deserializeTextNode() {
        final String xml = "<xpathpojo><one><two><three>value</three></two></one></xpathpojo>";

        final XPathPojo pojo = parser.fromXml(xml, XPathPojo.class);

        assertNotNull("POJO not deserialized", pojo);
        assertNotNull("POJO value not deserialized", pojo.value);
        assertEquals("POJO values not equal", "value", pojo.value);
    }

}
