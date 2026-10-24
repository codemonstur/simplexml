package unittests;

import model.WrappedPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        assertNotNull(actual, "Pojo is null");
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(newDefaultWrappedPojo());
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(wrapped);

        assertNotNull(actual, "No serialization response");
        assertEquals(wrappedXml, actual, "Invalid serialized output");
    }

    @Test
    public void deserializeNull() {
        final var actual1 = parser.fromXml(wrappedXmlNull, WrappedPojo.class);
        final var actual2 = parser.fromXml(wrappedXmlNullTwo, WrappedPojo.class);

        assertNotNull(actual1, "Pojo is null");
        assertNotNull(actual2, "Pojo is null");
        assertThat(actual1)
            .usingRecursiveComparison()
            .isEqualTo(wrappedNull);
        assertThat(actual2)
            .usingRecursiveComparison()
            .isEqualTo(wrappedNull);
    }

    @Test
    public void serializeNull() {
        final var actual = parser.toXml(wrappedNull);

        assertNotNull(actual, "No serialization response");
        assertEquals(wrappedXmlNull, actual, "Invalid serialized output");
    }

    private static WrappedPojo newDefaultWrappedPojo() {
        final Map<String, String> map = new HashMap<>();
        map.put("Guybrush", "Threepwood");
        final Set<String> set = new HashSet<>();
        set.add("Mêlée Island");
        return new WrappedPojo("Hello", List.of(", world"), map, set, new String[]{"monkey"});
    }

}
