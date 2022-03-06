package unittests;

import model.EnumPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static model.TestEnum.one;
import static org.junit.Assert.*;

public class EnumTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeEnumHasValue() {
        final String pojoXml = "<enumpojo><test>one</test></enumpojo>\n";

        final EnumPojo pojo = parser.fromXml(pojoXml, EnumPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", one, pojo.test);
    }

    @Test
    public void deserializeEnumEmpty() {
        final String pojoXml = "<enumpojo><test></test></enumpojo>\n";

        final EnumPojo pojo = parser.fromXml(pojoXml, EnumPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertNull("Pojo has the wrong name", pojo.test);
    }

    @Test
    public void deserializeEnumMissing() {
        final String pojoXml = "<enumpojo></enumpojo>\n";

        final EnumPojo pojo = parser.fromXml(pojoXml, EnumPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertNull("Pojo has the wrong name", pojo.test);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNoEnumOption() {
        final String pojoXml = "<enumpojo><test>four</test></enumpojo>\n";

        parser.fromXml(pojoXml, EnumPojo.class);

        fail("Missing enum value didn't throw exception");
    }

}
