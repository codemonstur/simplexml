package unittests;

import model.EnumPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static model.SimpleEnum.one;
import static model.SimpleEnum.value;
import static org.junit.Assert.*;

public class EnumTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeEnumHasValue() {
        final var input = "<enumpojo><test>one</test></enumpojo>\n";

        final var actual = parser.fromXml(input, EnumPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", one, actual.test);
    }

    @Test
    public void deserializeEnumEmpty() {
        final var input = "<enumpojo><test></test></enumpojo>\n";

        final var actual = parser.fromXml(input, EnumPojo.class);

        assertNotNull("Pojo is null", actual);
        assertNull("Pojo has the wrong name", actual.test);
    }

    @Test
    public void deserializeEnumMissing() {
        final var input = "<enumpojo></enumpojo>\n";

        final var actual = parser.fromXml(input, EnumPojo.class);

        assertNotNull("Pojo is null", actual);
        assertNull("Pojo has the wrong name", actual.test);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNoEnumOption() {
        final var input = "<enumpojo><test>missing</test></enumpojo>\n";

        parser.fromXml(input, EnumPojo.class);

        fail("Missing enum value didn't throw exception");
    }

    @Test
    public void serializeWithValueAnnotation() {
        final var input = new EnumPojo(value);
        final var expected = "<enumpojo>\n  <test>123</test>\n</enumpojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull("Pojo is null", actual);
        assertEquals("Invalid POJO XML", expected, actual);
    }

    @Test
    public void deserializeWithValueAnnotation() {
        final var pojoXml = "<enumpojo><test>123</test></enumpojo>\n";

        final var pojo = parser.fromXml(pojoXml, EnumPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", value, pojo.test);
    }

}
