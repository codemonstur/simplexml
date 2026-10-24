package unittests;

import model.Enum1Pojo;
import model.Enum2Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static model.EnumWithDefault.three;
import static model.SimpleEnum.one;
import static model.SimpleEnum.value;
import static org.junit.jupiter.api.Assertions.*;

public class EnumTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeEnumHasValue() {
        final var input = "<enum1pojo><test>one</test></enum1pojo>\n";

        final var actual = parser.fromXml(input, Enum1Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals(one, actual.test, "Pojo has the wrong name");
    }

    @Test
    public void deserializeEnumEmpty() {
        final var input = "<enum1pojo><test></test></enum1pojo>\n";

        final var actual = parser.fromXml(input, Enum1Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertNull(actual.test, "Pojo has the wrong name");
    }

    @Test
    public void deserializeEnumMissing() {
        final var input = "<enum1pojo></enum1pojo>\n";

        final var actual = parser.fromXml(input, Enum1Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertNull(actual.test, "Pojo has the wrong name");
    }

    @Test
    public void deserializeNoEnumOption() {
        final var input = "<enum1pojo><test>missing</test></enum1pojo>\n";

        assertThrows(IllegalArgumentException.class, () ->
            parser.fromXml(input, Enum1Pojo.class),
            "Missing enum value didn't throw exception");
    }

    @Test
    public void serializeWithValueAnnotation() {
        final var input = new Enum1Pojo(value);
        final var expected = "<enum1pojo>\n  <test>123</test>\n</enum1pojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "Pojo is null");
        assertEquals(expected, actual, "Invalid POJO XML");
    }

    @Test
    public void deserializeWithValueAnnotation() {
        final var pojoXml = "<enum1pojo><test>123</test></enum1pojo>\n";

        final var pojo = parser.fromXml(pojoXml, Enum1Pojo.class);

        assertNotNull(pojo, "Pojo is null");
        assertEquals(value, pojo.test, "Pojo has the wrong enum value");
    }

    @Test
    public void deserializeWithDefaultAnnotation() {
        final var pojoXml = "<enum2pojo><test>four</test></enum2pojo>\n";

        final var pojo = parser.fromXml(pojoXml, Enum2Pojo.class);

        assertNotNull(pojo, "Pojo is null");
        assertEquals(three, pojo.test, "Pojo has the wrong enum value");
    }

}
