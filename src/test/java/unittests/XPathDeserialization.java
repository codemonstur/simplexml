package unittests;

import model.XPathPojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XPathDeserialization {

    private SimpleXml simple = new SimpleXml();

    @Test
    public void deserializeXPath() throws IOException {
        final String xml = "<xpathpojo><one><two><three>value</three></two></one></xpathpojo>";

        final XPathPojo pojo = simple.fromXml(xml, XPathPojo.class);

        assertNotNull("POJO not deserialized", pojo);
        assertNotNull("POJO value not deserialized", pojo.value);
        assertEquals("POJO values not equal", "value", pojo.value);
    }

}
