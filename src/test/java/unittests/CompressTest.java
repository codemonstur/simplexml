package unittests;

import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void compress() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml(POM_WITH_WHITESPACE);
        assertEquals("POMs was not adequately compressed", POM_COMPRESSED, actual);
    }

    @Test
    public void commentIsRemoved() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<!-- a comment --><a>x</a>");
        assertEquals("Comment was not removed", "<a>x</a>", actual);
    }

    @Test
    public void commentContainingGreaterThanIsRemoved() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<!-- a > b --><a>x</a>");
        assertEquals("Comment containing a '>' was not removed", "<a>x</a>", actual);
    }

    @Test
    public void commentContainingLessThanIsRemoved() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<!-- a < b --><a>x</a>");
        assertEquals("Comment containing a '<' was not removed", "<a>x</a>", actual);
    }

    @Test
    public void multiLineCommentIsRemoved() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<r>\n  <!-- line1\n  line2 -->\n  <b>x</b>\n</r>");
        assertEquals("Multi-line comment was not removed", "<r><b>x</b></r>", actual);
    }

    @Test
    public void commentBetweenTextIsRemoved() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<r>one<!-- c -->two</r>");
        assertEquals("Comment between text was not removed", "<r>onetwo</r>", actual);
    }

    @Test
    public void prologContainingGreaterThanIsRemoved() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<?xml note=\"a>b\"?><a>x</a>");
        assertEquals("Prolog containing a '>' was not removed", "<a>x</a>", actual);
    }

    @Test
    public void selfClosingTagWithoutAttributes() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<b/>");
        assertEquals("Self closing tag without attributes was not written", "<b/>", actual);
    }

    @Test
    public void selfClosingTagWithoutAttributesAndWithASpace() {
        final var parser = new XmlParser();
        assertEquals("Self closing tag was not written", "<b/>", parser.compressXml("<b />"));
    }

    @Test
    public void selfClosingTagWithoutAttributesIsKeptWhenNested() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<root><b/></root>");
        assertEquals("Nested self closing tag without attributes was not written", "<root><b/></root>", actual);
    }

    @Test
    public void severalSelfClosingTagsWithoutAttributes() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<a><b/><c/><d /></a>");
        assertEquals("Self closing tags without attributes were not written", "<a><b/><c/><d/></a>", actual);
    }

    @Test
    public void selfClosingTagWithoutAttributesAfterText() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("one<a/>two<b></b>");
        assertEquals("Self closing tag without attributes was not written", "one<a/>two<b></b>", actual);
    }

    @Test
    public void selfClosingTagWithoutAttributesAfterAComment() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<!-- c --><a/>");
        assertEquals("Self closing tag after a comment was not written", "<a/>", actual);
    }

    @Test
    public void selfClosingTagWithoutAttributesAfterAProlog() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<?xml note=\"a>b\"?><a/>");
        assertEquals("Self closing tag after a prolog was not written", "<a/>", actual);
    }

    @Test
    public void selfClosingTagWithAttributes() {
        final var parser = new XmlParser();
        assertEquals("Self closing tag with attributes was not written", "<a x=\"1\"/>",
                parser.compressXml("<a x=\"1\"/>"));
        assertEquals("Self closing tag with attributes was not written", "<a x=\"1\"/>",
                parser.compressXml("<a x=\"1\" />"));
        assertEquals("Self closing tag with attributes was not written", "<a x=\"1\" y=\"2\"/>",
                parser.compressXml("<a  x = \"1\"  y = '2' />"));
    }

    @Test
    public void selfClosingTagWithAGreaterThanInItsValue() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<a x=\"y>z\"/>");
        assertEquals("Self closing tag with a '>' in its value was not written", "<a x=\"y>z\"/>", actual);
    }

    @Test
    public void selfClosingTagInsideCdataIsNotTouched() {
        final var parser = new XmlParser();
        final var actual = parser.compressXml("<a><![CDATA[<b/>]]></a>");
        assertEquals("CDATA containing a self closing tag was changed", "<a><![CDATA[<b/>]]></a>", actual);
    }

    @Test
    public void openingTagWithAttributesIsUnchanged() {
        final var parser = new XmlParser();
        assertEquals("Opening tag was not written", "<a x=\"1\">y</a>", parser.compressXml("<a x=\"1\">y</a>"));
        assertEquals("Opening tag was not written", "<a x=\"1\">y</a>", parser.compressXml("<a x=\"1\" >y</a>"));
        assertEquals("Opening tag was not written", "<a x=\"1\" y=\"2\">y</a>",
                parser.compressXml("<a  x = \"1\"  y = '2' >y</a>"));
    }

}
