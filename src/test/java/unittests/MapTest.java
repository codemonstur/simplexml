package unittests;

import model.MapPojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.error.InvalidAnnotation;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static xmlparser.model.XmlElement.newElement;

public class MapTest {

    private static class InvalidPojo {
        @XmlMapTagIsKey
        @XmlMapWithAttributes(keyName = "key")
        private Map<String, String> map;
    }

    private static final MapPojo map = newDefaultMapPojo();
    private static final String mapXml =
        "<mappojo>\n" +
        "  <map1>\n" +
        "    <1>monkey</1>\n" +
        "  </map1>\n" +
        "  <map2 key=\"2\" value=\"thumb\" />\n" +
        "  <map3>\n" +
        "    <key>3</key>\n" +
        "    <value>bonk</value>\n" +
        "  </map3>\n" +
        "  <map3>\n" +
        "    <key>4</key>\n" +
        "    <value>oiff</value>\n" +
        "  </map3>\n" +
        "  <map4 key=\"Mark\">Whatney</map4>\n" +
        "  <map5>\n" +
        "    <key>1</key>\n" +
        "    monkey\n" +
        "  </map5>\n" +
        "  <item key=\"2\" value=\"thumb\" />\n" +
        "</mappojo>\n";

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(mapXml, MapPojo.class);

        assertNotNull("Pojo is null", actual);
        assertReflectionEquals(newDefaultMapPojo(), actual);
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(map);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", mapXml, actual);
    }

    @Test(expected = InvalidAnnotation.class)
    public void twoMapTagsThrowsExceptionXml() {
        parser.fromXml("<invalidpojo><map></map></invalidpojo>", InvalidPojo.class);
    }

    @Test(expected = InvalidAnnotation.class)
    public void twoMapTagsThrowsExceptionDom() {
        final var xml = newElement("invalidpojo").child(newElement("map")).build();
        parser.fromXml(xml, InvalidPojo.class);
    }

    private static MapPojo newDefaultMapPojo() {
        final Map<Integer, String> map1 = new HashMap<>();
        map1.put(1, "monkey");
        final Map<Integer, String> map2 = new HashMap<>();
        map2.put(2, "thumb");
        final Map<Integer, String> map3 = new HashMap<>();
        map3.put(3, "bonk");
        map3.put(4, "oiff");
        final Map<String, String> map4 = new HashMap<>();
        map4.put("Mark", "Whatney");
        final Map<Integer, String> map5 = new HashMap<>();
        map5.put(1, "monkey");
        final Map<Integer, String> map6 = new HashMap<>();
        map6.put(2, "thumb");

        return new MapPojo(map1, map2, map3, map4, map5, map6);
    }

}
