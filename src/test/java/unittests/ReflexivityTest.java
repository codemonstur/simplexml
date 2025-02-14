package unittests;

import model.Enum1Pojo;
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
        final var pojo = new Enum1Pojo(SimpleEnum.one);

        final var actual = parser.fromXml(parser.toXml(pojo), Enum1Pojo.class);

        assertNotNull("No deserialization response", actual);
        assertEquals("Invalid serialized output", pojo.test, actual.test);
    }

}
