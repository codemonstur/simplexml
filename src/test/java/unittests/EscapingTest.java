package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.utils.Escaping;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscapingTest {

    @Test
    public void escapeXmlSpecials() {
        assertEquals("&lt;tag attr=&quot;a&amp;b&quot;&gt;", Escaping.escapeXml("<tag attr=\"a&b\">", false));
        assertEquals("&apos;x&apos;", Escaping.escapeXml("'x'", false));
    }

    @Test
    public void escapeXmlNullAndEmpty() {
        assertEquals(null, Escaping.escapeXml(null, false));
        assertEquals("", Escaping.escapeXml("", false));
    }

    @Test
    public void escapeXmlEncodeUtf8() {
        assertEquals("&#233;", Escaping.escapeXml("\u00e9", true));
        assertEquals("\u00e9", Escaping.escapeXml("\u00e9", false));
    }

    @Test
    public void escapeXmlSurrogatePair() {
        final String s = new String(Character.toChars(0x1F600));
        assertEquals(s, Escaping.escapeXml(s, false));
        assertEquals("&#128512;", Escaping.escapeXml(s, true));
    }

    @Test
    public void escapeXmlUnpairedSurrogate() {
        assertEquals("\uFFFD", Escaping.escapeXml("\uD800", false));
    }

    @Test
    public void unescapeXmlNamed() {
        assertEquals("<tag attr=\"a&b\">'x'", Escaping.unescapeXml("&lt;tag attr=&quot;a&amp;b&quot;&gt;&apos;x&apos;"));
    }

    @Test
    public void unescapeXmlNumericDecimal() {
        assertEquals("A", Escaping.unescapeXml("&#65;"));
        assertEquals("\u00e9", Escaping.unescapeXml("&#233;"));
    }

    @Test
    public void unescapeXmlNumericHex() {
        assertEquals("A", Escaping.unescapeXml("&#x41;"));
    }

    @Test
    public void unescapeXmlInvalidKept() {
        assertEquals("&#;", Escaping.unescapeXml("&#;"));
        assertEquals("&#xyz;", Escaping.unescapeXml("&#xyz;"));
        assertEquals("&#1;", Escaping.unescapeXml("&#1;")); // illegal control char kept
    }

    @Test
    public void unescapeXmlSurrogatePairRefs() {
        final String high = "&#xD83D;";
        final String low = "&#xDE00;";
        assertEquals(new String(Character.toChars(0x1F600)), Escaping.unescapeXml(high + low));
    }

    @Test
    public void unescapeXmlLoneSurrogateKept() {
        assertEquals("&#xD83D;", Escaping.unescapeXml("&#xD83D;"));
    }

    @Test
    public void escapeHtmlNamed() {
        assertEquals("&amp;&lt;", Escaping.escapeHtml("&<", false));
    }

    @Test
    public void unescapeHtmlNamed() {
        assertEquals("&<", Escaping.unescapeHtml("&amp;&lt;"));
    }

    @Test
    public void unescapeHtmlNumeric() {
        assertEquals("A", Escaping.unescapeHtml("&#65;"));
        assertEquals("A", Escaping.unescapeHtml("&#x41;"));
    }
}
