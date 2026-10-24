package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement.XmlTextElement;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static xmlparser.model.XmlElement.newElement;

public class DomTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void selfClosingParameter() {
        final var input = "<pojo><name>Hello</name><closing /></pojo>";

        final var actual = parser.fromXml(input);

        assertNotNull(actual, "Pojo is null");
        assertFalse(actual.isSelfClosing(), "<pojo> tag is self closing");
        assertFalse(actual.children.get(0).isSelfClosing(), "<name> tag is self closing");
        assertTrue(actual.children.get(1).isSelfClosing(), "<closing> tag is self closing");
    }

    @Test
    public void testGetElementsByTagName() {
        final var input = "<pojo><name>Hello</name></pojo>";
        final var actual = parser.fromXml(input).getElementsByTagName("name");

        assertNotNull(actual, "Pojo is null");
        assertEquals(1, actual.size(), "List size is incorrect");
        assertEquals("name", actual.get(0).name, "List item is incorrect");
    }

    @Test
    public void testToStringElement() {
        final var input = "<pojo><name>Hello</name></pojo>";
        final var actual = parser.fromXml(input);

        assertNotNull(actual, "Missing dom object");
        assertEquals("XmlElement[pojo]", actual.toString(), "String output is incorrect");
        assertEquals("XmlElement[name]", actual.children.get(0).toString(), "String output is incorrect");
    }

    @Test
    public void testToStringTextNode() {
        final var actual = newElement("pojo").text("hello").build();

        assertEquals("XmlElement[pojo]", actual.toString(), "String output is incorrect");
        assertEquals("XmlTextElement[hello]", actual.children.get(0).toString(), "String output is incorrect");
    }

    @Test
    public void testPrettyPrinting() {
        final var input = newElement("pojo")
                .child(newElement("child"))
                .text("hello").build();
        final var expected = "<pojo>\n  <child />\n  hello\n</pojo>\n";

        final var actual = parser.domToXml(input);

        assertNotNull(actual);
        assertEquals(expected, actual, "Invalid XML output");
    }

    @Test
    public void testTextBeforeAChildIsNotMovedAfterIt() {
        final var input = "<a>head<b>1</b></a>";
        final var expected = "<a>\n  head\n  <b>1</b>\n</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals(expected, actual, "Text before a child element was written after it");
    }

    @Test
    public void testTextAroundAChildKeepsItsPlace() {
        final var input = "<a>head<b>1</b>tail</a>";
        final var expected = "<a>\n  head\n  <b>1</b>\n  tail\n</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals(expected, actual, "Text was not written in document order");
    }

    @Test
    public void testTextBetweenChildrenIsNotMovedToTheEnd() {
        final var input = "<a>x<b>1</b>y<b>2</b>z</a>";
        final var expected = "<a>\n  x\n  <b>1</b>\n  y\n  <b>2</b>\n  z\n</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals(expected, actual, "Text between children was gathered at the end");
    }

    @Test
    public void testMixedContentIsStableAcrossARoundTrip() {
        final var input = "<root>one<t>two</t>three<u>four</u>five</root>";

        final var once = parser.domToXml(parser.fromXml(input));
        final var twice = parser.domToXml(parser.fromXml(once));

        assertEquals(once, twice, "Mixed content changed on re-reading the output");
        assertEquals("onethreefive", parser.fromXml(once).getText(), "Text content was lost");
    }

    @Test
    public void testTextNextToCDataIsWrittenAsASingleRun() {
        final var input = "<a>x<![CDATA[y<z>]]>z</a>";
        final var expected = "<a>xy&lt;z&gt;z</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals(expected, actual, "Neighbouring text nodes were split over several lines");
        assertEquals("xy<z>z", parser.fromXml(actual).getText(), "Text content was lost");
    }

    @Test
    public void toXmlOfADomTreeMatchesDomToXml() {
        final var element = parser.fromXml("<pojo attr=\"v\">head<child>1</child>tail</pojo>");

        assertEquals(parser.domToXml(element), parser.toXml(element),
                "toXml of a DOM tree did not agree with domToXml");
    }

    @Test
    public void toXmlOfADomTreeWritesTheWholeTree() {
        final var element = parser.fromXml("<a><b>1</b><c>2</c></a>");

        final var actual = parser.toXml(element);

        assertEquals("<a>\n  <b>1</b>\n  <c>2</c>\n</a>\n", actual, "The tree was not written");
    }

    @Test
    public void toXmlOfADomTreeThroughAWriter() throws IOException {
        final var element = parser.fromXml("<a><b>1</b></a>");
        final var writer = new StringWriter();

        parser.toXml(element, writer);

        assertEquals("<a>\n  <b>1</b>\n</a>\n", writer.toString(), "The tree was not written");
    }

    @Test
    public void toXmlOfADomTreeSurvivesBeingWrittenTwice() {
        final var element = parser.fromXml("<pojo attr=\"v\">head<child>1</child>tail</pojo>");

        final var once = parser.toXml(element);
        final var twice = parser.toXml(parser.fromXml(once));

        assertEquals(once, twice, "A DOM tree did not survive being written twice");
    }

    @Test
    public void toXmlStillWritesAnObjectAsBefore() {
        assertEquals("<pojo>\n  <name>hello</name>\n</pojo>\n",
            parser.toXml(new model.Pojo("hello")),
                "Object serialization changed");
    }

    @Test
    public void toXmlOfATextElementWritesItsText() {
        assertEquals("hi &amp; &lt;there&gt;",
            parser.toXml(new XmlTextElement(null, "hi & <there>")),
                "A text element was not written as its text");
    }

    @Test
    public void toXmlOfATextElementThroughAWriterWritesItsText() throws IOException {
        final var writer = new StringWriter();

        parser.toXml(new XmlTextElement(null, "hi & <there>"), writer);

        assertEquals("hi &amp; &lt;there&gt;", writer.toString(),
                "A text element was not written as its text");
    }

    @Test
    public void toXmlOfAnEmptyTextElementWritesNothing() {
        assertEquals("", parser.toXml(new XmlTextElement(null, "")),
                "An empty text element wrote something");
        assertEquals("", parser.toXml(new XmlTextElement(null, null)),
                "A text element without text wrote something");
    }

    @Test
    public void aTextElementStillHoldsNoChildren() {
        final var element = new XmlTextElement(null, "t");

        assertEquals(0, element.numChildrenWithName("c"), "A text element holds a child");
        assertNull(element.findChildForName("c", null), "A text element found a child");
        assertFalse(element.hasNonTextChildren(), "A text element has non-text children");
        assertEquals("t", element.getText(), "A text element lost its text");
    }

    @Test
    public void aTextElementCanBeGivenAChild() {
        final var element = new XmlTextElement(null, "t");

        element.appendChild(newElement("c").build());

        assertEquals(1, element.numChildrenWithName("c"), "A text element lost the child that was added");
        assertEquals("t", element.getText(), "A text element lost the text it was built with");
    }

    @Test
    public void toXmlStillWritesATextElementInsideARealElement() {
        final var root = newElement("r").child(new XmlTextElement(null, "t")).build();

        assertEquals("<r>t</r>\n", parser.toXml(root), "A text child was not written into its element");
    }

}
