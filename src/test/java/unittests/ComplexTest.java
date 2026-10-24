package unittests;

import model.ComplexPojo;
import model.SimpleEnum;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ComplexTest {

    private static final ComplexPojo complex = newDefaultComplexPojo();
    private static final String complexXml = """
            <complexpojo>
              <name>complex</name>
              <list>first</list>
              <list>second</list>
              <list>monkey</list>
              <map>
                <one>thumb</one>
              </map>
              <array>0.5</array>
              <array>34.8</array>
              <set>45.3</set>
              <set>1234567.9</set>
              <testenum>one</testenum>
            </complexpojo>
            """;

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(complexXml, ComplexPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(newDefaultComplexPojo());
    }

    @Test
    public void serialize() {
        final var actual = parser.toXml(complex);

        assertNotNull(actual, "No serialization response");
        assertEquals(complexXml, actual, "Invalid serialized output");
    }

    private static ComplexPojo newDefaultComplexPojo() {
        final List<String> list = Arrays.asList("first", "second", "monkey");
        final Map<String, String> map = new HashMap<>();
        map.put("one", "thumb");
        final Float[] array = { 0.5f, 34.8f };
        final Set<Double> set = new HashSet<>(Arrays.asList(45.3, 1234567.90));

        return new ComplexPojo("complex", list, map, array, set, SimpleEnum.one);
    }

}
