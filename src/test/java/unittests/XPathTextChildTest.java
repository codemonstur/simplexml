package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.xpath.XPathExpression.newXPath;

public class XPathTextChildTest {

    // The root element has a text node as its first child, which used to break xpath evaluation
    private static final String INPUT_TEXT_FIRST = "<pojo>some text<name>Hello</name></pojo>";
    private static final String INPUT_TEXT_FIRST_NESTED = "<pojo>some text<wrapper><name>Hello</name></wrapper></pojo>";

    private final XmlParser parser = new XmlParser();

    @Test
    public void evaluatePastTextChild() {
        final var document = parser.fromXml(INPUT_TEXT_FIRST);

        final var actual = newXPath("pojo/name").evaluateAny(document);

        assertNotNull("Element was not found", actual);
        assertEquals("Element has the wrong text", "Hello", actual.getText());
    }

    @Test
    public void evaluateDeepPastTextChild() {
        final var document = parser.fromXml(INPUT_TEXT_FIRST_NESTED);

        final var actual = newXPath("pojo/wrapper/name").evaluateAny(document);

        assertNotNull("Element was not found", actual);
        assertEquals("Element has the wrong text", "Hello", actual.getText());
    }

    @Test
    public void evaluateTextSegmentStillWorks() {
        final var document = parser.fromXml(INPUT_TEXT_FIRST);

        final var actual = newXPath("pojo/text()").evaluateAny(document);

        assertNotNull("Text node was not found", actual);
        assertEquals("Text node has the wrong text", "some text", ((XmlElement.XmlTextElement) actual).text);
    }

    @Test
    public void deserializeWithXPathPastTextChild() {
        final var actual = parser.fromXml(INPUT_TEXT_FIRST_NESTED, "pojo/wrapper", Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

}
