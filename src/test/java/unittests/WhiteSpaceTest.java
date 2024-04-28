package unittests;

import model.Pojo;
import model.ValidatorPojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class WhiteSpaceTest {

    @Test
    public void whitespaceIsRemovedByDefault() {
        final XmlParser parser = new XmlParser();
        final String pojoXml = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "Hello", pojo.name);
    }

    @Test
    public void whitespaceIsConserved() {
        final XmlParser parser = newXmlParser().conserveWhitespace().build();
        final String pojoXml = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "    Hello   \n  ", pojo.name);
    }

    @Test
    public void whitespaceIsConservedInDOM() {
        final XmlParser parser = newXmlParser().conserveWhitespace().build();
        final String pojoXml = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final XmlElement pojo = parser.fromXml(pojoXml);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has wrong number of children", 3, pojo.children.size());
        assertEquals("First whitespace is incorrect", "\n  ", pojo.children.get(0).getText());
        assertEquals("Second whitespace is incorrect", "\n", pojo.children.get(2).getText());
    }

    @Test
    public void parsingOfXMLwithLeadingCommentAndConservedWhitespaceWorks() {
        final XmlParser parser = newXmlParser().conserveWhitespace().build();
        final String pojoXml = """
            <!-- leading comment -->
            <pojo>
              <name>    Hello   \n  </name>
            </pojo>
            """;

        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name", "    Hello   \n  ", pojo.name);
    }

    @Test
    public void parsingOfXMLwithLeadingPrologAndConservedWhitespaceWorks() {
        final XmlParser parser = newXmlParser().build();
        final String pojoXml = """
            <?xml version="1.0"?>
            <pojo>
              <name>    Hello   \n  </name>
            </pojo>
            """;

        final Pojo pojo = parser.fromXml(pojoXml, Pojo.class);

        assertNotNull("Pojo is null", pojo);
        assertEquals("Pojo has the wrong name value", "Hello", pojo.name);
    }

}
