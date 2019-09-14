package unittests;

import model.XPathPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XPathDeserialization {

    private XmlParser parser = new XmlParser();

    @Test
    public void deserializeXPath() {
        final String xml = "<xpathpojo><one><two><three>value</three></two></one></xpathpojo>";

        final XPathPojo pojo = parser.fromXml(xml, XPathPojo.class);

        assertNotNull("POJO not deserialized", pojo);
        assertNotNull("POJO value not deserialized", pojo.value);
        assertEquals("POJO values not equal", "value", pojo.value);
    }

}
