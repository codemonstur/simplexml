package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement.XmlTextElement;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static xmlparser.model.XmlElement.newElement;

public class DomTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void selfClosingParameter() {
        final var input = "<pojo><name>Hello</name><closing /></pojo>";

        final var actual = parser.fromXml(input);

        assertNotNull("Pojo is null", actual);
        assertFalse("<pojo> tag is self closing", actual.isSelfClosing());
        assertFalse("<name> tag is self closing", actual.children.get(0).isSelfClosing());
        assertTrue("<closing> tag is self closing", actual.children.get(1).isSelfClosing());
    }

    @Test
    public void testGetElementsByTagName() {
        final var input = "<pojo><name>Hello</name></pojo>";
        final var actual = parser.fromXml(input).getElementsByTagName("name");

        assertNotNull("Pojo is null", actual);
        assertEquals("List size is incorrect", 1, actual.size());
        assertEquals("List item is incorrect", "name", actual.get(0).name);
    }

    @Test
    public void testToStringElement() {
        final var input = "<pojo><name>Hello</name></pojo>";
        final var actual = parser.fromXml(input);

        assertNotNull("Missing dom object", actual);
        assertEquals("String output is incorrect", "XmlElement[pojo]", actual.toString());
        assertEquals("String output is incorrect", "XmlElement[name]", actual.children.get(0).toString());
    }

    @Test
    public void testToStringTextNode() {
        final var actual = newElement("pojo").text("hello").build();

        assertEquals("String output is incorrect", "XmlElement[pojo]", actual.toString());
        assertEquals("String output is incorrect", "XmlTextElement[hello]", actual.children.get(0).toString());
    }

    @Test
    public void testPrettyPrinting() {
        final var input = newElement("pojo")
                .child(newElement("child"))
                .text("hello").build();
        final var expected = "<pojo>\n  <child />\n  hello\n</pojo>\n";

        final var actual = parser.domToXml(input);

        assertNotNull(actual);
        assertEquals("Invalid XML output", expected, actual);
    }

    @Test
    public void testTextBeforeAChildIsNotMovedAfterIt() {
        final var input = "<a>head<b>1</b></a>";
        final var expected = "<a>\n  head\n  <b>1</b>\n</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals("Text before a child element was written after it", expected, actual);
    }

    @Test
    public void testTextAroundAChildKeepsItsPlace() {
        final var input = "<a>head<b>1</b>tail</a>";
        final var expected = "<a>\n  head\n  <b>1</b>\n  tail\n</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals("Text was not written in document order", expected, actual);
    }

    @Test
    public void testTextBetweenChildrenIsNotMovedToTheEnd() {
        final var input = "<a>x<b>1</b>y<b>2</b>z</a>";
        final var expected = "<a>\n  x\n  <b>1</b>\n  y\n  <b>2</b>\n  z\n</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals("Text between children was gathered at the end", expected, actual);
    }

    @Test
    public void testMixedContentIsStableAcrossARoundTrip() {
        final var input = "<root>one<t>two</t>three<u>four</u>five</root>";

        final var once = parser.domToXml(parser.fromXml(input));
        final var twice = parser.domToXml(parser.fromXml(once));

        assertEquals("Mixed content changed on re-reading the output", once, twice);
        assertEquals("Text content was lost",
                "onethreefive", parser.fromXml(once).getText());
    }

    @Test
    public void testTextNextToCDataIsWrittenAsASingleRun() {
        final var input = "<a>x<![CDATA[y<z>]]>z</a>";
        final var expected = "<a>xy&lt;z&gt;z</a>\n";

        final var actual = parser.domToXml(parser.fromXml(input));

        assertEquals("Neighbouring text nodes were split over several lines", expected, actual);
        assertEquals("Text content was lost", "xy<z>z", parser.fromXml(actual).getText());
    }

    @Test
    public void toXmlOfADomTreeMatchesDomToXml() {
        final var element = parser.fromXml("<pojo attr=\"v\">head<child>1</child>tail</pojo>");

        assertEquals("toXml of a DOM tree did not agree with domToXml",
                parser.domToXml(element), parser.toXml(element));
    }

    @Test
    public void toXmlOfADomTreeWritesTheWholeTree() {
        final var element = parser.fromXml("<a><b>1</b><c>2</c></a>");

        final var actual = parser.toXml(element);

        assertEquals("The tree was not written", "<a>\n  <b>1</b>\n  <c>2</c>\n</a>\n", actual);
    }

    @Test
    public void toXmlOfADomTreeThroughAWriter() throws IOException {
        final var element = parser.fromXml("<a><b>1</b></a>");
        final var writer = new StringWriter();

        parser.toXml(element, writer);

        assertEquals("The tree was not written", "<a>\n  <b>1</b>\n</a>\n", writer.toString());
    }

    @Test
    public void toXmlOfADomTreeSurvivesBeingWrittenTwice() {
        final var element = parser.fromXml("<pojo attr=\"v\">head<child>1</child>tail</pojo>");

        final var once = parser.toXml(element);
        final var twice = parser.toXml(parser.fromXml(once));

        assertEquals("A DOM tree did not survive being written twice", once, twice);
    }

    @Test
    public void toXmlStillWritesAnObjectAsBefore() {
        assertEquals("Object serialization changed",
                "<pojo>\n  <name>hello</name>\n</pojo>\n", parser.toXml(new model.Pojo("hello")));
    }

    @Test
    public void toXmlOfATextElementWritesItsText() {
        assertEquals("A text element was not written as its text",
                "hi &amp; &lt;there&gt;", parser.toXml(new XmlTextElement(null, "hi & <there>")));
    }

    @Test
    public void toXmlOfATextElementThroughAWriterWritesItsText() throws IOException {
        final var writer = new StringWriter();

        parser.toXml(new XmlTextElement(null, "hi & <there>"), writer);

        assertEquals("A text element was not written as its text", "hi &amp; &lt;there&gt;", writer.toString());
    }

    @Test
    public void toXmlOfAnEmptyTextElementWritesNothing() {
        assertEquals("An empty text element wrote something", "", parser.toXml(new XmlTextElement(null, "")));
        assertEquals("A text element without text wrote something", "", parser.toXml(new XmlTextElement(null, null)));
    }

    @Test
    public void aTextElementStillHoldsNoChildren() {
        final var element = new XmlTextElement(null, "t");

        assertEquals("A text element holds a child", 0, element.numChildrenWithName("c"));
        assertNull("A text element found a child", element.findChildForName("c", null));
        assertFalse("A text element has non-text children", element.hasNonTextChildren());
        assertEquals("A text element lost its text", "t", element.getText());
    }

    @Test
    public void aTextElementCanBeGivenAChild() {
        final var element = new XmlTextElement(null, "t");

        element.appendChild(newElement("c").build());

        assertEquals("A text element lost the child that was added", 1, element.numChildrenWithName("c"));
        assertEquals("A text element lost the text it was built with", "t", element.getText());
    }

    @Test
    public void toXmlStillWritesATextElementInsideARealElement() {
        final var root = newElement("r").child(new XmlTextElement(null, "t")).build();

        assertEquals("A text child was not written into its element", "<r>t</r>\n", parser.toXml(root));
    }

}
