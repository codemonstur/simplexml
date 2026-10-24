package unittests;

import model.PrimitiveArrayPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitiveArrayTest {

    private static final String POJO_XML = """
            <primitivearraypojo>
              <numbers>1</numbers>
              <numbers>2</numbers>
              <booleans>true</booleans>
              <booleans>false</booleans>
              <strings>one</strings>
              <strings>two</strings>
            </primitivearraypojo>
            """;

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(POJO_XML, PrimitiveArrayPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertArrayEquals(new int[]{1, 2}, actual.numbers, "Numbers have the wrong contents");
        assertArrayEquals(new boolean[]{true, false}, actual.booleans, "Booleans have the wrong contents");
        assertArrayEquals(new String[]{"one", "two"}, actual.strings, "Strings have the wrong contents");
    }

    @Test
    public void deserializeEmpty() {
        final var actual = parser.fromXml("<primitivearraypojo></primitivearraypojo>", PrimitiveArrayPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertArrayEquals(new int[0], actual.numbers, "Numbers should be empty");
    }

    @Test
    public void serialize() {
        final var input = new PrimitiveArrayPojo(new int[]{1, 2}, new boolean[]{true, false}, new String[]{"one", "two"});

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(POJO_XML, actual, "Invalid serialized output");
    }

}
