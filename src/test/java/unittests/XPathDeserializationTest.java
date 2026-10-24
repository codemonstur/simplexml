package unittests;

import model.Pojo;
import model.XPathPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XPathDeserializationTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeObject() {
        final var input = "<xpathpojo><wrapper><object>Hello</object></wrapper></xpathpojo>";

        final var actual = parser.fromXml(input, XPathPojo.class);

        assertNotNull(actual, "POJO not deserialized");
        assertNotNull(actual.object, "POJO value not deserialized");
        assertEquals("Hello", actual.object, "POJO values not equal");
    }

    @Test
    public void deserializeAttribute() {
        final var input = "<xpathpojo><wrapper attr=\"hello\" /></xpathpojo>";

        final var actual = parser.fromXml(input, XPathPojo.class);

        assertNotNull(actual, "POJO not deserialized");
        assertNotNull(actual.attr, "POJO value not deserialized");
        assertEquals("hello", actual.attr, "POJO values not equal");
    }

    @Test
    public void deserializeTextNode() {
        final var input = "<xpathpojo><one><two><three>value</three></two></one></xpathpojo>";

        final var actual = parser.fromXml(input, XPathPojo.class);

        assertNotNull(actual, "POJO not deserialized");
        assertNotNull(actual.value, "POJO value not deserialized");
        assertEquals("value", actual.value, "POJO values not equal");
    }

    @Test
    public void deserializeWithAbsolutePath() {
        final var input = "<root><a><pojo><name>Hello</name></pojo></a></root>";

        final var actual = parser.fromXml(input, "/root/a/pojo", Pojo.class);

        assertNotNull(actual, "Pojo not deserialized with an absolute path");
        assertEquals("Hello", actual.name, "Pojo values not equal");
    }

    @Test
    public void deserializeWithAbsolutePathFindsTheSamePojoAsARelativeOne() {
        final var input = "<root><a><pojo><name>Hello</name></pojo></a></root>";

        assertEquals(
            parser.fromXml(input, "root/a/pojo", Pojo.class).name,
            parser.fromXml(input, "/root/a/pojo", Pojo.class).name,
            "Absolute and relative paths disagree");
    }

}
