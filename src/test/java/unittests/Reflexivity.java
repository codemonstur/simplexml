package unittests;

import model.EnumPojo;
import model.SimpleEnum;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class Reflexivity {

    private XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void reflexiveEnum() {
        final EnumPojo pojo = new EnumPojo(SimpleEnum.one);

        final EnumPojo result = parser.fromXml(parser.toXml(pojo), EnumPojo.class);

        assertNotNull("No deserialization response", result);
        assertEquals("Invalid serialized output", pojo.test, result.test);
    }

}
