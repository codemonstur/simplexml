package unittests;

import model.StaticFieldPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertNotNull;

public class StaticFieldTest {

    private XmlParser parser = new XmlParser();

    @Test
    public void deserializeObject() {
        final String pojoXml = "<staticfieldpojo></staticfieldpojo>\n";

        final StaticFieldPojo pojo = parser.fromXml(pojoXml, StaticFieldPojo.class);

        assertNotNull("Pojo is null", pojo);
    }

}
