package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.*;

public class DomTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void selfClosingParameter() {
        final String pojoXml = "<pojo><name>Hello</name><closing /></pojo>";

        final XmlElement pojo = parser.fromXml(pojoXml);

        assertNotNull("Pojo is null", pojo);
        assertFalse("<pojo> tag is self closing", pojo.isSelfClosing());
        assertFalse("<name> tag is self closing", pojo.children.get(0).isSelfClosing());
        assertTrue("<closing> tag is self closing", pojo.children.get(1).isSelfClosing());
    }

}
