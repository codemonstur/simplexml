package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompressTest {

    private static final String POM_WITH_WHITESPACE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<  project xmlns=   http://maven.apache.org/POM/4.0.0      \t\n   " +
        "    xmlns:xsi   =    \"http://www.w3.org/2001/XMLSchema-instance\"" +
        "    xsi:schemaLocation   =\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
        "<groupId>       com.github.codemonstur</groupId>" +
        "     <artifactId>simplexml       </artifactId>     " +
        "<version>1.0.0</version>\n\n\n\n" +
        "\t\t</project>\n\n\n";

    private static final String POM_COMPRESSED = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"><groupId>com.github.codemonstur</groupId><artifactId>simplexml</artifactId><version>1.0.0</version></project>";

    private final XmlParser parser = new XmlParser();

    @Test
    public void compress() {
        final var actual = parser.compressXml(POM_WITH_WHITESPACE);
        assertEquals(POM_COMPRESSED, actual, "POMs was not adequately compressed");
    }

    @Test
    public void commentIsRemoved() {
        final var actual = parser.compressXml("<!-- a comment --><a>x</a>");
        assertEquals("<a>x</a>", actual, "Comment was not removed");
    }

    @Test
    public void commentContainingGreaterThanIsRemoved() {
        final var actual = parser.compressXml("<!-- a > b --><a>x</a>");
        assertEquals("<a>x</a>", actual, "Comment containing a '>' was not removed");
    }

    @Test
    public void commentContainingLessThanIsRemoved() {
        final var actual = parser.compressXml("<!-- a < b --><a>x</a>");
        assertEquals("<a>x</a>", actual, "Comment containing a '<' was not removed");
    }

    @Test
    public void multiLineCommentIsRemoved() {
        final var actual = parser.compressXml("<r>\n  <!-- line1\n  line2 -->\n  <b>x</b>\n</r>");
        assertEquals("<r><b>x</b></r>", actual, "Multi-line comment was not removed");
    }

    @Test
    public void commentBetweenTextIsRemoved() {
        final var actual = parser.compressXml("<r>one<!-- c -->two</r>");
        assertEquals("<r>onetwo</r>", actual, "Comment between text was not removed");
    }

    @Test
    public void prologContainingGreaterThanIsRemoved() {
        final var actual = parser.compressXml("<?xml note=\"a>b\"?><a>x</a>");
        assertEquals("<a>x</a>", actual, "Prolog containing a '>' was not removed");
    }

    @Test
    public void selfClosingTagWithoutAttributes() {
        final var actual = parser.compressXml("<b/>");
        assertEquals("<b/>", actual, "Self closing tag without attributes was not written");
    }

    @Test
    public void selfClosingTagWithoutAttributesAndWithASpace() {
        assertEquals("<b/>", parser.compressXml("<b />"), "Self closing tag was not written");
    }

    @Test
    public void selfClosingTagWithoutAttributesIsKeptWhenNested() {
        final var actual = parser.compressXml("<root><b/></root>");
        assertEquals("<root><b/></root>", actual, "Nested self closing tag without attributes was not written");
    }

    @Test
    public void severalSelfClosingTagsWithoutAttributes() {
        final var actual = parser.compressXml("<a><b/><c/><d /></a>");
        assertEquals("<a><b/><c/><d/></a>", actual, "Self closing tags without attributes were not written");
    }

    @Test
    public void selfClosingTagWithoutAttributesAfterText() {
        final var actual = parser.compressXml("one<a/>two<b></b>");
        assertEquals("one<a/>two<b></b>", actual, "Self closing tag without attributes was not written");
    }

    @Test
    public void selfClosingTagWithoutAttributesAfterAComment() {
        final var actual = parser.compressXml("<!-- c --><a/>");
        assertEquals("<a/>", actual, "Self closing tag after a comment was not written");
    }

    @Test
    public void selfClosingTagWithoutAttributesAfterAProlog() {
        final var actual = parser.compressXml("<?xml note=\"a>b\"?><a/>");
        assertEquals("<a/>", actual, "Self closing tag after a prolog was not written");
    }

    @Test
    public void selfClosingTagWithAttributes() {
        assertEquals("<a x=\"1\"/>", parser.compressXml("<a x=\"1\"/>"),
                "Self closing tag with attributes was not written");
        assertEquals("<a x=\"1\"/>", parser.compressXml("<a x=\"1\" />"),
                "Self closing tag with attributes was not written");
        assertEquals("<a x=\"1\" y=\"2\"/>", parser.compressXml("<a  x = \"1\"  y = '2' />"),
                "Self closing tag with attributes was not written");
    }

    @Test
    public void selfClosingTagWithAGreaterThanInItsValue() {
        final var actual = parser.compressXml("<a x=\"y>z\"/>");
        assertEquals("<a x=\"y>z\"/>", actual, "Self closing tag with a '>' in its value was not written");
    }

    @Test
    public void selfClosingTagInsideCdataIsNotTouched() {
        final var actual = parser.compressXml("<a><![CDATA[<b/>]]></a>");
        assertEquals("<a><![CDATA[<b/>]]></a>", actual, "CDATA containing a self closing tag was changed");
    }

    @Test
    public void openingTagWithAttributesIsUnchanged() {
        assertEquals("<a x=\"1\">y</a>", parser.compressXml("<a x=\"1\">y</a>"), "Opening tag was not written");
        assertEquals("<a x=\"1\">y</a>", parser.compressXml("<a x=\"1\" >y</a>"), "Opening tag was not written");
        assertEquals("<a x=\"1\" y=\"2\">y</a>", parser.compressXml("<a  x = \"1\"  y = '2' >y</a>"), "Opening tag was not written");
    }

}
