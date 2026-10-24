package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlFieldDeserializer;
import xmlparser.error.InvalidAnnotation;
import xmlparser.model.XmlElement;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        final var input = """
            <fieldpojo>
                <map>
                    <item name="ItemA" value="123" />
                    <item name="ItemB" value="qwerty" />
                    <item name="ItemC" value="ABC" />
                </map>
            </fieldpojo>""";

        final var actual = parser.fromXml(input, FieldPojo.class);

        assertNotNull(actual, "pojo is null");
        assertNotNull(actual.map, "map is null");
        assertFalse(actual.map.isEmpty(), "map is empty");
        assertEquals(3, actual.map.size(), "map has wrong number of elements");
    }

    @Test
    public void testInvalidFieldDeserializer() {
        final var input = "<invalidfieldpojo><map></map></invalidfieldpojo>";
        assertThrows(InvalidAnnotation.class, () ->
            parser.fromXml(input, InvalidFieldPojo.class));
    }

}
