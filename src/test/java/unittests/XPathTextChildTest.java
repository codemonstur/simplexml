package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        assertNotNull(actual, "Element was not found");
        assertEquals("Hello", actual.getText(), "Element has the wrong text");
    }

    @Test
    public void evaluateDeepPastTextChild() {
        final var document = parser.fromXml(INPUT_TEXT_FIRST_NESTED);

        final var actual = newXPath("pojo/wrapper/name").evaluateAny(document);

        assertNotNull(actual, "Element was not found");
        assertEquals("Hello", actual.getText(), "Element has the wrong text");
    }

    @Test
    public void evaluateTextSegmentStillWorks() {
        final var document = parser.fromXml(INPUT_TEXT_FIRST);

        final var actual = newXPath("pojo/text()").evaluateAny(document);

        assertNotNull(actual, "Text node was not found");
        assertEquals("some text", ((XmlElement.XmlTextElement) actual).text, "Text node has the wrong text");
    }

    @Test
    public void deserializeWithXPathPastTextChild() {
        final var actual = parser.fromXml(INPUT_TEXT_FIRST_NESTED, "pojo/wrapper", Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

}
