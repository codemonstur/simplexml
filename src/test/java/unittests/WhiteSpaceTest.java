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
        final var parser = new XmlParser();
        final var input = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test
    public void whitespaceIsConserved() {
        final var parser = newXmlParser().conserveWhitespace().build();
        final var input = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "    Hello   \n  ", actual.name);
    }

    @Test
    public void whitespaceIsConservedInDOM() {
        final var parser = newXmlParser().conserveWhitespace().build();
        final var input = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final var actual = parser.fromXml(input);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has wrong number of children", 3, actual.children.size());
        assertEquals("First whitespace is incorrect", "\n  ", actual.children.get(0).getText());
        assertEquals("Second whitespace is incorrect", "\n", actual.children.get(2).getText());
    }

    @Test
    public void parsingOfXMLwithLeadingCommentAndConservedWhitespaceWorks() {
        final var parser = newXmlParser().conserveWhitespace().build();
        final var input = """
            <!-- leading comment -->
            <pojo>
              <name>    Hello   \n  </name>
            </pojo>
            """;

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "    Hello   \n  ", actual.name);
    }

    @Test
    public void parsingOfXMLwithLeadingPrologAndConservedWhitespaceWorks() {
        final var parser = newXmlParser().build();
        final var input = """
            <?xml version="1.0"?>
            <pojo>
              <name>    Hello   \n  </name>
            </pojo>
            """;

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name value", "Hello", actual.name);
    }

}
