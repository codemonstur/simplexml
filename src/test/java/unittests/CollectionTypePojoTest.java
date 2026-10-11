package unittests;

import model.CollectionTypePojo;
import model.CollectionTypeRecord;
import org.junit.Test;
import xmlparser.XmlParser;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        assertNotNull("Pojo is null", actual);
        assertEquals("List has the wrong contents", List.of("one", "two"), actual.list);
        assertEquals("Set has the wrong contents", java.util.Set.of("three"), actual.set);
        assertEquals("Map has the wrong contents", java.util.Map.of("key", "value"), actual.map);
        assertEquals("Collection has the wrong contents", List.of("four", "five"), List.copyOf(actual.collection));
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(new CollectionTypePojo());

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", POJO_XML, actual);
    }

    @Test
    public void deserializeRecord() {
        final var actual = parser.fromXml(RECORD_XML, CollectionTypeRecord.class);

        assertNotNull("Record is null", actual);
        assertEquals("List has the wrong contents", List.of("one", "two"), actual.list());
        assertEquals("Set has the wrong contents", java.util.Set.of("three"), actual.set());
        assertEquals("Map has the wrong contents", java.util.Map.of("key", "value"), actual.map());
        assertEquals("Collection has the wrong contents", List.of("four", "five"), List.copyOf(actual.collection()));
    }

    @Test
    public void serializeRecord() {
        final var actual = parser.toXml(CollectionTypeRecord.example());

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", RECORD_XML, actual);
    }

}
