package unittests;

import model.PojoWithAttribute;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void greaterThanInQuotedAttribute() {
        assertEquals("y>z", parser.fromXml("<a b=\"y>z\">q</a>").attributes.get("b"),
                "Attribute value was truncated at the '>'");
    }

    @Test
    public void textAfterGreaterThanAttributeIsKept() {
        assertEquals("q", parser.fromXml("<a b=\"y>z\">q</a>").getText(),
                "Text following a '>' in an attribute was corrupted");
    }

    @Test
    public void attributeAfterOneContainingGreaterThan() {
        final var attributes = parser.fromXml("<a b=\"y>z\" c=\"2\">q</a>").attributes;

        assertEquals("2", attributes.get("c"), "Second attribute was lost");
        assertEquals("y>z", attributes.get("b"), "First attribute was corrupted");
    }

    @Test
    public void greaterThanInSingleQuotedAttribute() {
        assertEquals("y>z", parser.fromXml("<a b='y>z'>q</a>").attributes.get("b"),
                "Attribute value was truncated at the '>'");
    }

    @Test
    public void mixedQuoteStylesEachContainGreaterThan() {
        final var attributes = parser.fromXml("<a b=\"x>y\" c='p>q'>z</a>").attributes;

        assertEquals("x>y", attributes.get("b"), "Double quoted attribute was corrupted");
        assertEquals("p>q", attributes.get("c"), "Single quoted attribute was corrupted");
        assertEquals("z", parser.fromXml("<a b=\"x>y\" c='p>q'>z</a>").getText(),
                "Text after the attributes was corrupted");
    }

    @Test
    public void lessThanInQuotedAttribute() {
        assertEquals("1<2", parser.fromXml("<a b=\"1<2\">q</a>").attributes.get("b"),
                "Attribute value containing '<' was corrupted");
    }

    @Test
    public void singleQuotedAttributeWithSpace() {
        assertEquals("x y", parser.fromXml("<a b='x y'>q</a>").attributes.get("b"),
                "Single quoted attribute containing whitespace was not read");
    }

    @Test
    public void deserializeAttributeContainingGreaterThan() {
        final var actual = parser.fromXml("<pojowithattribute name=\"y>z\" />", PojoWithAttribute.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("y>z", actual.name, "Attribute containing '>' was not deserialized");
    }

    @Test
    public void compressKeepsGreaterThanInAttribute() {
        assertEquals("<a b=\"y>z\">q</a>", parser.compressXml("<a b=\"y>z\">q</a>"),
                "Compressing dropped part of the attribute");
    }

    @Test
    public void compressKeepsGreaterThanInSingleQuotedAttribute() {
        assertEquals("<a b=\"y>z\">q</a>", parser.compressXml("<a b='y>z'>q</a>"),
                "Compressing dropped part of the attribute");
    }

    @Test
    public void unterminatedQuotedValue() {
        assertThrows(InvalidXml.class, () ->
            parser.fromXml("<a b='don't'>q</a>"),
            "Unterminated quoted attribute value not detected");
    }

}
