package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.utils.Trimming;
import xmlparser.utils.Trimming.Trim;
import xmlparser.utils.XmlParse;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class XmlParseTest {

    private static final Trim XML = new Trimming.XmlTrimmer();

    @Test
    public void getNameOfTagStopsAtWhitespaceOrSlash() {
        assertEquals("tag", XmlParse.getNameOfTag("tag attr", XML));
        assertEquals("tag", XmlParse.getNameOfTag("tag/>", XML));
        assertEquals("", XmlParse.getNameOfTag("/>", XML));
        assertEquals("", XmlParse.getNameOfTag("   attr", XML));
    }

    @Test
    public void readLineReturnsTextBeforeEnd() throws Exception {
        assertEquals("abc", XmlParse.readLine(new StringReader("abc<rest"), XML, '<'));
    }

    @Test
    public void readLineReturnsNullAtEnd() throws Exception {
        assertNull(XmlParse.readLine(new StringReader(""), XML, '<'));
    }

    @Test
    public void readLineThrowsOnLeftoverText() {
        assertThrows(xmlparser.error.InvalidXml.class,
            () -> XmlParse.readLine(new StringReader("abc"), XML, '<'));
    }

    @Test
    public void readLineTrimsLeftoverCheck() throws Exception {
        // trailing whitespace is trimmed before the "leftover text" check, so this is not an error
        assertNull(XmlParse.readLine(new StringReader("   "), XML, '<'));
    }

    @Test
    public void readCDataStripsMarkers() throws Exception {
        assertEquals("hello", XmlParse.readCData(new StringReader(""), "![CDATA[hello]]"));
    }

    @Test
    public void readCDataHandlesEmbeddedGreaterThan() throws Exception {
        assertEquals("a>b", XmlParse.readCData(new StringReader("b]]>"), "![CDATA[a"));
    }

    @Test
    public void readCDataUnterminatedThrows() {
        assertThrows(xmlparser.error.InvalidXml.class,
            () -> XmlParse.readCData(new StringReader("nope"), "![CDATA[x"));
    }

    @Test
    public void readTagSimple() throws Exception {
        assertEquals("tag", XmlParse.readTag(new StringReader("tag>")));
    }

    @Test
    public void readTagKeepsQuotedGreaterThan() throws Exception {
        assertEquals("tag attr=\"a>b\"", XmlParse.readTag(new StringReader("tag attr=\"a>b\">")));
    }

    @Test
    public void readTagComment() throws Exception {
        assertEquals("!-- comment --", XmlParse.readTag(new StringReader("!-- comment -->")));
    }

    @Test
    public void readTagProlog() throws Exception {
        assertEquals("?xml version=\"1.0\"?", XmlParse.readTag(new StringReader("?xml version=\"1.0\"?>")));
    }

    @Test
    public void readTagUnterminatedQuoteThrows() {
        assertThrows(xmlparser.error.InvalidXml.class,
            () -> XmlParse.readTag(new StringReader("tag attr=\"unterminated")));
    }

    @Test
    public void readTagEndOfInputReturnsNull() throws Exception {
        assertNull(XmlParse.readTag(new StringReader("")));
    }

    @Test
    public void readUntilReturnsMarker() throws Exception {
        assertEquals("abEND", XmlParse.readUntil(new StringReader("abENDrest"), "END"));
    }

    @Test
    public void readUntilNullWhenMissing() throws Exception {
        assertNull(XmlParse.readUntil(new StringReader("abc"), "END"));
    }

    @Test
    public void finishTagComment() throws Exception {
        assertEquals("-->", XmlParse.finishTag(new StringReader("-->"), "!-- comment "));
    }

    @Test
    public void finishTagCommentAlreadyClosed() throws Exception {
        assertNull(XmlParse.finishTag(new StringReader("ignored"), "!-- comment --"));
    }

    @Test
    public void finishTagDeclarationWithOpenSubset() throws Exception {
        assertEquals("]>", XmlParse.finishTag(new StringReader("]>"), "!DOCTYPE r ["));
    }

    @Test
    public void finishTagDeclarationWithoutSubset() throws Exception {
        assertNull(XmlParse.finishTag(new StringReader("ignored"), "<!DOCTYPE r"));
    }

    @Test
    public void hasOpenSubsetTracksQuotes() {
        assertEquals(true, XmlParse.hasOpenSubset("!DOCTYPE r ["));
        assertEquals(false, XmlParse.hasOpenSubset("!DOCTYPE r []"));
        assertEquals(false, XmlParse.hasOpenSubset("!DOCTYPE r [']' ]"));
        assertEquals(false, XmlParse.hasOpenSubset("!DOCTYPE r ['[' ]"));
    }

    @Test
    public void indexOfNonWhitespaceChar() {
        assertEquals(2, XmlParse.indexOfNonWhitespaceChar("  x", 0, XML));
        assertEquals(-1, XmlParse.indexOfNonWhitespaceChar("   ", 0, XML));
    }

    @Test
    public void indexOfWhitespaceChar() {
        assertEquals(2, XmlParse.indexOfWhitespaceChar("ab c", 0, XML));
        assertEquals(-1, XmlParse.indexOfWhitespaceChar("abc", 0, XML));
    }
}
