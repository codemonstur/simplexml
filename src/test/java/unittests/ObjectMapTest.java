package unittests;

import model.ObjectMapPojo;
import model.Pojo;
import model.SimpleEnum;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectMapTest {

    private static final String XML = """
        <objectmappojo>
          <tagIsKeyValue>
            <x>
              <name>monkey</name>
            </x>
          </tagIsKeyValue>
          <tagIsKeyKey>
          </tagIsKeyKey>
          <childNodeEntries>
            <key>
              <name>one</name>
            </key>
            <value>
              <name>bonk</name>
            </value>
          </childNodeEntries>
          <enumKeys>
            <two>1</two>
          </enumKeys>
          <enumValues key="k" value="two" />
        </objectmappojo>
        """;

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeObjectValues() {
        final var actual = parser.fromXml(XML, ObjectMapPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(1, actual.tagIsKeyValue.size(), "Map has the wrong size");
        assertEquals("monkey", actual.tagIsKeyValue.get("x").name, "Map has the wrong value");
        assertEquals("one", actual.childNodeEntries.keySet().iterator().next().name, "Map has the wrong key");
        assertEquals("bonk", actual.childNodeEntries.values().iterator().next().name, "Map has the wrong value");
        assertEquals("1", actual.enumKeys.get(SimpleEnum.two), "Map has the wrong enum key");
        assertEquals(SimpleEnum.two, actual.enumValues.get("k"), "Map has the wrong enum value");
    }

    @Test
    public void serializeObjectValues() {
        final var actual = parser.toXml(newPojo());

        assertNotNull(actual, "No serialization response");
        assertEquals(XML, actual, "Invalid serialized output");
    }

    @Test
    public void roundTripsObjectKeysAndValues() {
        final var actual = parser.fromXml(parser.toXml(newPojo()), ObjectMapPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("one", actual.childNodeEntries.keySet().iterator().next().name, "Map has the wrong key");
        assertEquals("bonk", actual.childNodeEntries.values().iterator().next().name, "Map has the wrong value");
        assertEquals("monkey", actual.tagIsKeyValue.get("x").name, "Map has the wrong value");
    }

    /**
     * A key that is only a tag name or an attribute value can not be turned back into an object,
     * so those entries are skipped the same way a null key is.
     */
    @Test
    public void unrepresentableObjectKeysAreSkipped() {
        final var actual = parser.fromXml(
                "<objectmappojo><tagIsKeyKey><x>1</x></tagIsKeyKey></objectmappojo>", ObjectMapPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(Map.of(), actual.tagIsKeyKey, "Entry with an unrepresentable key should have been skipped");
    }

    /**
     * A tag name has to start with a letter, so an @XmlEnumValue that does not can not be a
     * @XmlMapTagIsKey key. Writing it would produce XML that can not be read back.
     */
    @Test
    public void enumKeyThatIsNotATagNameThrows() {
        final Map<SimpleEnum, String> keys = new HashMap<>();
        keys.put(SimpleEnum.value, "1");

        assertThrows(InvalidXml.class, () ->
            parser.toXml(new ObjectMapPojo(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
                keys, new HashMap<>())),
                "Enum key that is not a valid tag name was written");
    }

    /**
     * A value that is only an attribute value can not be turned back into an object, so it reads
     * back as a null entry, the same way a missing value does.
     */
    @Test
    public void unrepresentableObjectValuesAreNull() {
        final var actual = parser.fromXml(
                "<objectmappojo><attributeValues key=\"k\" value=\"v\"/></objectmappojo>", ObjectMapPojo.class);

        final Map<String, Pojo> expected = new HashMap<>();
        expected.put("k", null);

        assertNotNull(actual, "Pojo is null");
        assertEquals(expected, actual.attributeValues, "Map has the wrong contents");
    }

    private static ObjectMapPojo newPojo() {
        final Map<String, Pojo> tagIsKeyValue = new HashMap<>();
        tagIsKeyValue.put("x", new Pojo("monkey"));

        final Map<Pojo, Pojo> childNodeEntries = new HashMap<>();
        childNodeEntries.put(new Pojo("one"), new Pojo("bonk"));

        final Map<SimpleEnum, String> enumKeys = new HashMap<>();
        enumKeys.put(SimpleEnum.two, "1");

        final Map<String, SimpleEnum> enumValues = new HashMap<>();
        enumValues.put("k", SimpleEnum.two);

        return new ObjectMapPojo(tagIsKeyValue, new HashMap<>(), childNodeEntries, new HashMap<>(),
                enumKeys, enumValues);
    }

}
