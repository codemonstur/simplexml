package unittests;

import model.Enum1Pojo;
import model.Enum2Pojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static model.EnumWithDefault.three;
import static model.SimpleEnum.one;
import static model.SimpleEnum.value;
import static org.junit.Assert.*;

public class EnumTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeEnumHasValue() {
        final var input = "<enum1pojo><test>one</test></enum1pojo>\n";

        final var actual = parser.fromXml(input, Enum1Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", one, actual.test);
    }

    @Test
    public void deserializeEnumEmpty() {
        final var input = "<enum1pojo><test></test></enum1pojo>\n";

        final var actual = parser.fromXml(input, Enum1Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertNull("Pojo has the wrong name", actual.test);
    }

    @Test
    public void deserializeEnumMissing() {
        final var input = "<enum1pojo></enum1pojo>\n";

        final var actual = parser.fromXml(input, Enum1Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertNull("Pojo has the wrong name", actual.test);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNoEnumOption() {
        final var input = "<enum1pojo><test>missing</test></enum1pojo>\n";

        parser.fromXml(input, Enum1Pojo.class);

        fail("Missing enum value didn't throw exception");
    }

    @Test
    public void serializeWithValueAnnotation() {
        final var input = new Enum1Pojo(value);
        final var expected = "<enum1pojo>\n  <test>123</test>\n</enum1pojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull("Pojo is null", actual);
        assertEquals("Invalid POJO XML", expected, actual);
    }

    @Test
    public void deserializeWithValueAnnotation() {
        final var pojoXml = "<enum1pojo><test>123</test></enum1pojo>\n";

        final var pojo = parser.fromXml(pojoXml, Enum1Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong enum value", value, pojo.test);
    }

    @Test
    public void deserializeWithDefaultAnnotation() {
        final var pojoXml = "<enum2pojo><test>four</test></enum2pojo>\n";

        final var pojo = parser.fromXml(pojoXml, Enum2Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong enum value", three, pojo.test);
    }
}
