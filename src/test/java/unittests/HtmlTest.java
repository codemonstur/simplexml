package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class HtmlTest {

    private XmlParser parser = newXmlParser().unescapeHtml().build();

    @Test
    public void deserializeEscapedHtml() {
        final String pojoXml = "<pojo>\n  <name>&copy;&#123;&#x0026;&#x26;</name>\n</pojo>\n";
        final Pojo pojo1 = new Pojo("©{&&");

        final Pojo pojo2 = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("No serialization response", pojo2);
        assertEquals("Invalid serialized output", pojo1.name, pojo2.name);
    }

}
