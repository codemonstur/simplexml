package unittests;

import model.EnumPojo;
import model.SimpleEnum;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class ReflexivityTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void reflexiveEnum() {
        final var pojo = new EnumPojo(SimpleEnum.one);

        final var actual = parser.fromXml(parser.toXml(pojo), EnumPojo.class);

        assertNotNull("No deserialization response", actual);
        assertEquals("Invalid serialized output", pojo.test, actual.test);
    }

}
