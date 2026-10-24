package unittests;

import model.Enum1Pojo;
import model.SimpleEnum;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class ReflexivityTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void reflexiveEnum() {
        final var pojo = new Enum1Pojo(SimpleEnum.one);

        final var actual = parser.fromXml(parser.toXml(pojo), Enum1Pojo.class);

        assertNotNull(actual, "No deserialization response");
        assertEquals(pojo.test, actual.test, "Invalid serialized output");
    }

}
