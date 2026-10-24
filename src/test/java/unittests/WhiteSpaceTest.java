package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class WhiteSpaceTest {

    @Test
    public void whitespaceIsRemovedByDefault() {
        final var parser = new XmlParser();
        final var input = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void multilineTagWithoutAttributes() {
        final var parser = newXmlParser().build();
        final var input = "<tag\n></tag>";

        final var actual = parser.fromXml(input);

        assertNotNull(actual, "XmlElement is null");
        assertEquals("tag", actual.name, "XmlElement has the wrong name value");
        assertEquals(0, actual.attributes.size(), "XmlElement has wrong attribute count");
    }

    @Test
    public void multilineTagWithAttributes() {
        final var parser = newXmlParser().build();
        final var input = "<tag\n attr=true></tag>";

        final var actual = parser.fromXml(input);

        assertNotNull(actual, "XmlElement is null");
        assertEquals("tag", actual.name, "XmlElement has the wrong name value");
        assertEquals(1, actual.attributes.size(), "XmlElement has wrong attribute count");
        assertEquals("true", actual.attributes.get("attr"), "XmlElement has wrong attribute");
    }

    @Test
    public void whitespaceIsConserved() {
        final var parser = newXmlParser().conserveWhitespace().build();
        final var input = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("    Hello   \n  ", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void whitespaceIsConservedInDOM() {
        final var parser = newXmlParser().conserveWhitespace().build();
        final var input = "<pojo>\n  <name>    Hello   \n  </name>\n</pojo>\n";

        final var actual = parser.fromXml(input);

        assertNotNull(actual, "Pojo is null");
        assertEquals(3, actual.children.size(), "Pojo has wrong number of children");
        assertEquals("\n  ", actual.children.get(0).getText(), "First whitespace is incorrect");
        assertEquals("\n", actual.children.get(2).getText(), "Second whitespace is incorrect");
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

        assertNotNull(actual, "Pojo is null");
        assertEquals("    Hello   \n  ", actual.name, "Pojo has the wrong name");
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

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name value");
    }

}
