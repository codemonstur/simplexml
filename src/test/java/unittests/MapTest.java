package unittests;

import model.MapPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class MapTest {

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
        final MapPojo pojo = parser.fromXml(mapXml, MapPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertReflectionEquals(newDefaultMapPojo(), pojo);
    }

    @Test
    public void serialize() {
        final String xml = parser.toXml(map);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", mapXml, xml);
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
