package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlFieldDeserializer;
import xmlparser.error.InvalidAnnotation;
import xmlparser.model.XmlElement;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FieldDeserializerTest {

    private static class FieldPojo {
        @XmlFieldDeserializer(clazz="unittests.FieldDeserializerTest", function="deserializeMap")
        private Map<String, String> map;
    }

    private static class InvalidFieldPojo {
        @XmlFieldDeserializer(clazz="doesntexist.Clazz", function="nothere")
        private Map<String, String> map;
    }

    // It is used, but the IDE can't see this
    public static Map<String, String> deserializeMap(final XmlElement parent) {
        final Map<String, String> map = new HashMap<>();
        for (final XmlElement item : parent.findChildForName("map", null).children) {
            map.put(item.attributes.get("name"), item.attributes.get("value"));
        }
        return map;
    }

    private final XmlParser parser = new XmlParser();

    @Test
    public void testFieldDeserializer() {
        final String xml =
            "<fieldpojo>\n" +
            "    <map>\n" +
            "        <item name=\"ItemA\" value=\"123\" />\n" +
            "        <item name=\"ItemB\" value=\"qwerty\" />\n" +
            "        <item name=\"ItemC\" value=\"ABC\" />\n" +
            "    </map>\n" +
            "</fieldpojo>";

        final FieldPojo pojo = parser.fromXml(xml, FieldPojo.class);

        assertNotNull("pojo is null", pojo);
        assertNotNull("map is null", pojo.map);
        assertFalse("map is empty", pojo.map.isEmpty());
        assertEquals("map has wrong number of elements", 3, pojo.map.size());
    }

    @Test (expected = InvalidAnnotation.class)
    public void testInvalidFieldDeserializer() {
        final String xml = "<invalidfieldpojo><map></map></invalidfieldpojo>";
        parser.fromXml(xml, InvalidFieldPojo.class);
    }
}
