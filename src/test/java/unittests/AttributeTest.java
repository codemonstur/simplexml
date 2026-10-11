package unittests;

import model.PojoWithAttribute;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.error.InvalidXml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AttributeTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void greaterThanInQuotedAttribute() {
        assertEquals("Attribute value was truncated at the '>'", "y>z",
            parser.fromXml("<a b=\"y>z\">q</a>").attributes.get("b"));
    }

    @Test
    public void textAfterGreaterThanAttributeIsKept() {
        assertEquals("Text following a '>' in an attribute was corrupted", "q",
            parser.fromXml("<a b=\"y>z\">q</a>").getText());
    }

    @Test
    public void attributeAfterOneContainingGreaterThan() {
        final var attributes = parser.fromXml("<a b=\"y>z\" c=\"2\">q</a>").attributes;

        assertEquals("Second attribute was lost", "2", attributes.get("c"));
        assertEquals("First attribute was corrupted", "y>z", attributes.get("b"));
    }

    @Test
    public void greaterThanInSingleQuotedAttribute() {
        assertEquals("Attribute value was truncated at the '>'", "y>z",
            parser.fromXml("<a b='y>z'>q</a>").attributes.get("b"));
    }

    @Test
    public void mixedQuoteStylesEachContainGreaterThan() {
        final var attributes = parser.fromXml("<a b=\"x>y\" c='p>q'>z</a>").attributes;

        assertEquals("Double quoted attribute was corrupted", "x>y", attributes.get("b"));
        assertEquals("Single quoted attribute was corrupted", "p>q", attributes.get("c"));
        assertEquals("Text after the attributes was corrupted", "z",
            parser.fromXml("<a b=\"x>y\" c='p>q'>z</a>").getText());
    }

    @Test
    public void lessThanInQuotedAttribute() {
        assertEquals("Attribute value containing '<' was corrupted", "1<2",
            parser.fromXml("<a b=\"1<2\">q</a>").attributes.get("b"));
    }

    @Test
    public void singleQuotedAttributeWithSpace() {
        assertEquals("Single quoted attribute containing whitespace was not read", "x y",
            parser.fromXml("<a b='x y'>q</a>").attributes.get("b"));
    }

    @Test
    public void deserializeAttributeContainingGreaterThan() {
        final var actual = parser.fromXml("<pojowithattribute name=\"y>z\" />", PojoWithAttribute.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Attribute containing '>' was not deserialized", "y>z", actual.name);
    }

    @Test
    public void compressKeepsGreaterThanInAttribute() {
        assertEquals("Compressing dropped part of the attribute", "<a b=\"y>z\">q</a>",
            parser.compressXml("<a b=\"y>z\">q</a>"));
    }

    @Test
    public void compressKeepsGreaterThanInSingleQuotedAttribute() {
        assertEquals("Compressing dropped part of the attribute", "<a b=\"y>z\">q</a>",
            parser.compressXml("<a b='y>z'>q</a>"));
    }

    @Test(expected = InvalidXml.class)
    public void unterminatedQuotedValue() {
        parser.fromXml("<a b='don't'>q</a>");
        fail("Unterminated quoted attribute value not detected");
    }

}
