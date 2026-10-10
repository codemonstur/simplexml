package unittests;

import model.WrappedPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class WrappedTest {

    private static final WrappedPojo wrapped = newDefaultWrappedPojo();
    private static final String wrappedXml = """
            <wrappedpojo>
              <wrapper1>
                <string>Hello</string>
              </wrapper1>
              <wrapper2>
                <list>, world</list>
              </wrapper2>
              <wrapper3>
                <map>
                  <Guybrush>Threepwood</Guybrush>
                </map>
              </wrapper3>
              <wrapper4>
                <set>Mêlée Island</set>
              </wrapper4>
              <wrapper5>
                <array>monkey</array>
              </wrapper5>
            </wrappedpojo>
            """;

    private static final WrappedPojo wrappedNull = new WrappedPojo(null, null, null, null, null);
    private static final String wrappedXmlNull = "<wrappedpojo />\n";
    private static final String wrappedXmlNullTwo = "<wrappedpojo><wrapper1></wrapper1></wrappedpojo>";

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(wrappedXml, WrappedPojo.class);

        assertNotNull("Pojo is null", actual);
        assertReflectionEquals(newDefaultWrappedPojo(), actual);
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(wrapped);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", wrappedXml, actual);
    }

    @Test
    public void deserializeNull() {
        final var actual1 = parser.fromXml(wrappedXmlNull, WrappedPojo.class);
        final var actual2 = parser.fromXml(wrappedXmlNullTwo, WrappedPojo.class);

        assertNotNull("Pojo is null", actual1);
        assertNotNull("Pojo is null", actual2);
        assertReflectionEquals(wrappedNull, actual1);
        assertReflectionEquals(wrappedNull, actual2);
    }

    @Test
    public void serializeNull() {
        final var actual = parser.toXml(wrappedNull);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", wrappedXmlNull, actual);
    }

    private static WrappedPojo newDefaultWrappedPojo() {
        final Map<String, String> map = new HashMap<>();
        map.put("Guybrush", "Threepwood");
        final Set<String> set = new HashSet<>();
        set.add("Mêlée Island");
        return new WrappedPojo("Hello", List.of(", world"), map, set, new String[]{"monkey"});
    }

}
