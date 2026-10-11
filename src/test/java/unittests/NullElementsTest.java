package unittests;

import model.NullElementsPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", XML, actual);
    }

    @Test
    public void nullElementsAreDeserialized() {
        final var actual = parser.fromXml(XML, NullElementsPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("List has the wrong contents", Arrays.asList("a", null, "c"), actual.list);
        assertEquals("Set has the wrong contents", new HashSet<>(Arrays.asList("b", null, "d")), actual.set);
        assertArrayEquals("Array has the wrong contents", new String[]{"x", null, "z"}, actual.array);
        assertEquals("Map has the wrong contents", mapOf("k", "v", "nul", null), actual.map);
        assertEquals("Map with attributes has the wrong contents",
                mapOf("key1", "val1", "nul", null), actual.mapWithAttributes);
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

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
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
