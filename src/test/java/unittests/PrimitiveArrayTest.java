package unittests;

import model.PrimitiveArrayPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        assertNotNull("Pojo is null", actual);
        assertArrayEquals("Numbers have the wrong contents", new int[]{1, 2}, actual.numbers);
        assertArrayEquals("Booleans have the wrong contents", new boolean[]{true, false}, actual.booleans);
        assertArrayEquals("Strings have the wrong contents", new String[]{"one", "two"}, actual.strings);
    }

    @Test
    public void deserializeEmpty() {
        final var actual = parser.fromXml("<primitivearraypojo></primitivearraypojo>", PrimitiveArrayPojo.class);

        assertNotNull("Pojo is null", actual);
        assertArrayEquals("Numbers should be empty", new int[0], actual.numbers);
    }

    @Test
    public void serialize() {
        final var input = new PrimitiveArrayPojo(new int[]{1, 2}, new boolean[]{true, false}, new String[]{"one", "two"});

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", POJO_XML, actual);
    }

}
