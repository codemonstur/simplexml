package unittests;

import model.NullElementsPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NullElementsTest {

    private static final String XML = """
            <nullelementspojo>
              <list>a</list>
              <list/>
              <list>c</list>
              <set>b</set>
              <set/>
              <set>d</set>
              <array>x</array>
              <array/>
              <array>z</array>
              <map>
                <k>v</k>
                <nul/>
              </map>
              <map2 key="key1">val1</map2>
              <map2 key="nul" />
            </nullelementspojo>
            """;

    private final XmlParser parser = new XmlParser();

    private static NullElementsPojo withNullElements() {
        final Map<String, String> map = new LinkedHashMap<>();
        map.put("k", "v");
        map.put("nul", null);
        final Map<String, String> mapWithAttributes = new LinkedHashMap<>();
        mapWithAttributes.put("key1", "val1");
        mapWithAttributes.put("nul", null);
        return new NullElementsPojo(
                new ArrayList<>(Arrays.asList("a", null, "c")),
                new LinkedHashSet<>(Arrays.asList("b", null, "d")),
                new String[]{"x", null, "z"},
                map,
                mapWithAttributes);
    }

    @Test
    public void nullElementsAreSerialized() {
        final var actual = parser.toXml(withNullElements());

        assertNotNull(actual, "No serialization response");
        assertEquals(XML, actual, "Invalid serialized output");
    }

    @Test
    public void nullElementsAreDeserialized() {
        final var actual = parser.fromXml(XML, NullElementsPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(Arrays.asList("a", null, "c"), actual.list, "List has the wrong contents");
        assertEquals(new HashSet<>(Arrays.asList("b", null, "d")), actual.set, "Set has the wrong contents");
        assertArrayEquals(new String[]{"x", null, "z"}, actual.array, "Array has the wrong contents");
        assertEquals(mapOf("k", "v", "nul", null), actual.map, "Map has the wrong contents");
        assertEquals(mapOf("key1", "val1", "nul", null), actual.mapWithAttributes,
                "Map with attributes has the wrong contents");
    }

    @Test
    public void nullMapKeyIsSkipped() {
        final var input = new NullElementsPojo();
        input.map = new LinkedHashMap<>();
        input.map.put("good", "v");
        input.map.put(null, "ignored");
        input.mapWithAttributes = new LinkedHashMap<>();
        input.mapWithAttributes.put(null, "ignored");
        final var expected = """
                <nullelementspojo>
                  <map>
                    <good>v</good>
                  </map>
                </nullelementspojo>
                """;

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    private static Map<String, String> mapOf(final String key, final String value) {
        final Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    private static Map<String, String> mapOf(final String key1, final String value1, final String key2, final String value2) {
        final Map<String, String> map = mapOf(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
