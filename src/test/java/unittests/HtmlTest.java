package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class HtmlTest {

    private final XmlParser parser = newXmlParser().escapeHtml().unescapeHtml().build();

    @Test
    public void deserializeEscapedHtml() {
        final var input = "<pojo>\n  <name>&copy;&#123;&#x0026;&#x26;&quot;&apos;</name>\n</pojo>\n";
        final var expected = new Pojo("©{&&\"'");

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected.name, actual.name, "Invalid serialized output");
    }

    @Test
    public void serializeHtml() {
        final var input = new Pojo("©{&\"'<>");
        final var expected = "<pojo>\n  <name>&copy;&lcub;&amp;&quot;&apos;&lt;&gt;</name>\n</pojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

}
