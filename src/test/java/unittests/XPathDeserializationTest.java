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
        final var input = "<xpathpojo><wrapper><object>Hello</object></wrapper></xpathpojo>";

        final var actual = parser.fromXml(input, XPathPojo.class);

        assertNotNull("POJO not deserialized", actual);
        assertNotNull("POJO value not deserialized", actual.object);
        assertEquals("POJO values not equal", "Hello", actual.object);
    }

    @Test
    public void deserializeAttribute() {
        final var input = "<xpathpojo><wrapper attr=\"hello\" /></xpathpojo>";

        final var actual = parser.fromXml(input, XPathPojo.class);

        assertNotNull("POJO not deserialized", actual);
        assertNotNull("POJO value not deserialized", actual.attr);
        assertEquals("POJO values not equal", "hello", actual.attr);
    }

    @Test
    public void deserializeTextNode() {
        final var input = "<xpathpojo><one><two><three>value</three></two></one></xpathpojo>";

        final var actual = parser.fromXml(input, XPathPojo.class);

        assertNotNull("POJO not deserialized", actual);
        assertNotNull("POJO value not deserialized", actual.value);
        assertEquals("POJO values not equal", "value", actual.value);
    }

}
