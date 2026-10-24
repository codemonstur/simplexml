package unittests;

import model.MapPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlMapWithChildNodes;
import xmlparser.error.InvalidAnnotation;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static xmlparser.model.XmlElement.newElement;

public class MapTest {

    private static class InvalidPojo {
        @XmlMapTagIsKey
        @XmlMapWithAttributes(keyName = "key")
        private Map<String, String> map;
    }

    private static class NullValuePojo {
        @XmlMapWithAttributes(keyName = "key", valueName = "value")
        private Map<String, Integer> attributes;
        @XmlMapWithAttributes(keyName = "key")
        private Map<String, Integer> textAttributes;
        @XmlMapWithChildNodes(keyName = "key", valueName = "value")
        private Map<String, Integer> childNodes;
        @XmlMapWithChildNodes(keyName = "key")
        private Map<String, Integer> textChildNodes;
    }

    private static final MapPojo map = newDefaultMapPojo();
    private static final String mapXml = """
        <mappojo>
          <map1>
            <one>monkey</one>
          </map1>
          <map2 key="2" value="thumb" />
          <map3>
            <key>3</key>
            <value>bonk</value>
          </map3>
          <map3>
            <key>4</key>
            <value>oiff</value>
          </map3>
          <map4 key="Mark">Whatney</map4>
          <map5>
            <key>1</key>
            monkey
          </map5>
          <item key="2" value="thumb" />
        </mappojo>
        """;

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(mapXml, MapPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(newDefaultMapPojo());
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(map);

        assertNotNull(actual, "No serialization response");
        assertEquals(mapXml, actual, "Invalid serialized output");
    }

    @Test
    public void twoMapTagsThrowsExceptionXml() {
        assertThrows(InvalidAnnotation.class, () ->
            parser.fromXml("<invalidpojo><map></map></invalidpojo>", InvalidPojo.class));
    }

    @Test
    public void twoMapTagsThrowsExceptionDom() {
        final var xml = newElement("invalidpojo").child(newElement("map")).build();
        assertThrows(InvalidAnnotation.class, () ->
                parser.fromXml(xml, InvalidPojo.class));
    }

    @Test
    public void missingValueAttributeIsNull() {
        final var actual = parser.fromXml(
                "<nullvaluepojo><attributes key=\"k\"/></nullvaluepojo>", NullValuePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(mapWith("k", null), actual.attributes, "Map has the wrong contents");
    }

    @Test
    public void missingValueTextIsNull() {
        final var actual = parser.fromXml(
                "<nullvaluepojo><textAttributes key=\"k\"/></nullvaluepojo>", NullValuePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(mapWith("k", null), actual.textAttributes, "Map has the wrong contents");
    }

    @Test
    public void keyElementWithoutTextIsSkipped() {
        final var actual = parser.fromXml(
                "<nullvaluepojo><childNodes><key/><value>1</value></childNodes></nullvaluepojo>", NullValuePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(new HashMap<>(), actual.childNodes, "Entry without a key should have been skipped");
    }

    @Test
    public void valueElementWithoutTextIsNull() {
        final var actual = parser.fromXml(
                "<nullvaluepojo><childNodes><key>k</key><value/></childNodes></nullvaluepojo>", NullValuePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(mapWith("k", null), actual.childNodes, "Map has the wrong contents");
    }

    @Test
    public void nullMapValueRoundTrips() {
        final var pojo = new NullValuePojo();
        pojo.attributes = mapWith("k", null);
        final var expected = """
            <nullvaluepojo>
              <attributes key="k" />
            </nullvaluepojo>
            """;

        final var xml = parser.toXml(pojo);
        assertEquals(expected, xml, "Invalid serialized output");

        final var actual = parser.fromXml(xml, NullValuePojo.class);
        assertNotNull(actual, "Pojo is null");
        assertEquals(mapWith("k", null), actual.attributes, "Map has the wrong contents");
    }

    private static Map<String, Integer> mapWith(final String key, final Integer value) {
        final Map<String, Integer> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private static MapPojo newDefaultMapPojo() {
        final Map<String, String> map1 = new HashMap<>();
        map1.put("one", "monkey");
        final Map<String, String> map2 = new HashMap<>();
        map2.put("2", "thumb");
        final Map<String, String> map3 = new HashMap<>();
        map3.put("3", "bonk");
        map3.put("4", "oiff");
        final Map<String, String> map4 = new HashMap<>();
        map4.put("Mark", "Whatney");
        final Map<String, String> map5 = new HashMap<>();
        map5.put("1", "monkey");
        final Map<String, String> map6 = new HashMap<>();
        map6.put("2", "thumb");

        return new MapPojo(map1, map2, map3, map4, map5, map6);
    }

}
