package unittests;

import model.CollectionTypePojo;
import model.CollectionTypeRecord;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CollectionTypePojoTest {

    private static final String POJO_XML = """
            <collectiontypepojo>
              <list>one</list>
              <list>two</list>
              <set>three</set>
              <map>
                <key>value</key>
              </map>
              <collection>four</collection>
              <collection>five</collection>
            </collectiontypepojo>
            """;

    private static final String RECORD_XML = """
            <collectiontyperecord>
              <list>one</list>
              <list>two</list>
              <set>three</set>
              <map>
                <key>value</key>
              </map>
              <collection>four</collection>
              <collection>five</collection>
            </collectiontyperecord>
            """;

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(POJO_XML, CollectionTypePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(List.of("one", "two"), actual.list, "List has the wrong contents");
        assertEquals(Set.of("three"), actual.set, "Set has the wrong contents");
        assertEquals(Map.of("key", "value"), actual.map, "Map has the wrong contents");
        assertEquals(List.of("four", "five"), List.copyOf(actual.collection), "Collection has the wrong contents");
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(new CollectionTypePojo());

        assertNotNull(actual, "No serialization response");
        assertEquals(POJO_XML, actual, "Invalid serialized output");
    }

    @Test
    public void deserializeRecord() {
        final var actual = parser.fromXml(RECORD_XML, CollectionTypeRecord.class);

        assertNotNull(actual, "Record is null");
        assertEquals(List.of("one", "two"), actual.list(), "List has the wrong contents");
        assertEquals(Set.of("three"), actual.set(), "Set has the wrong contents");
        assertEquals(Map.of("key", "value"), actual.map(), "Map has the wrong contents");
        assertEquals(List.of("four", "five"), List.copyOf(actual.collection()), "Collection has the wrong contents");
    }

    @Test
    public void serializeRecord() {
        final var actual = parser.toXml(CollectionTypeRecord.example());

        assertNotNull(actual, "No serialization response");
        assertEquals(RECORD_XML, actual, "Invalid serialized output");
    }

}
