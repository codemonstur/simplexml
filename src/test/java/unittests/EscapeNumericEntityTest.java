package unittests;

import model.Pojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;
import static xmlparser.utils.Escaping.*;

public class EscapeNumericEntityTest {

    @Test
    public void unescapeDecimalXml() {
        assertEquals("A", unescapeXml("&#65;"), "Invalid decimal entity");
        assertEquals("©{&", unescapeXml("&#169;&#123;&#38;"), "Invalid decimal entity");
    }

    @Test
    public void unescapeHexXml() {
        assertEquals("A", unescapeXml("&#x41;"), "Invalid hexadecimal entity");
        assertEquals("J", unescapeXml("&#x4a;"), "Invalid hexadecimal entity");
        assertEquals("&", unescapeXml("&#x0026;"), "Invalid hexadecimal entity");
    }

    @Test
    public void unescapeCodePointsBeyondBmpXml() {
        assertEquals("\ud83d\ude00", unescapeXml("&#128512;"), "Invalid code point");
        assertEquals("\ud83d\ude00", unescapeXml("&#x1f600;"), "Invalid code point");
    }

    @Test
    public void unescapeSurrogateReferencesAreJoinedIntoOneCharacter() {
        // A supplementary character used to be escaped as two references, one to each half
        assertEquals("\ud83d\ude00", unescapeXml("&#55357;&#56832;"),
                "Surrogate references were not joined");
        assertEquals( "\ud83d\ude00", unescapeXml("&#xD83D;&#xDE00;"),
                "Surrogate references were not joined");
        assertEquals("\ud83d\ude00", unescapeHtml("&#55357;&#56832;"),
                "Surrogate references were not joined");
    }

    @Test
    public void unescapeLoneSurrogateReferenceIsKeptAsText() {
        assertEquals("&#55296;", unescapeXml("&#55296;"), "A surrogate without its partner was decoded");
        assertEquals("&#56320;", unescapeXml("&#56320;"), "A surrogate without its partner was decoded");
        assertEquals("&#55357;A", unescapeXml("&#55357;&#65;"), "A high surrogate was joined to a character that is not its partner");
        assertEquals("&#55296;", unescapeHtml("&#55296;"), "A surrogate without its partner was decoded");
    }

    @Test
    public void escapeSupplementaryCharacterIsASingleReference() {
        assertEquals("&#128512;", escapeXml("\ud83d\ude00", true),
                "A surrogate pair was escaped as a reference to each half");
        assertEquals("&#128512;", escapeHtml("\ud83d\ude00", true),
                "A surrogate pair was escaped as a reference to each half");
        assertEquals("\ud83d\ude00", escapeXml("\ud83d\ude00", false),
                "A surrogate pair was changed");
    }

    @Test
    public void escapeLoneSurrogateIsReplaced() {
        assertEquals("a\uFFFDb", escapeXml("a\uD800b", false),
                "An unpaired surrogate was written to the document");
        assertEquals("a\uFFFDb", escapeXml("a\uD800b", true),
                "An unpaired surrogate was written to the document");
        assertEquals("a\uFFFDb", escapeXml("a\uDC00b", false),
                "An unpaired surrogate was written to the document");
        assertEquals("a\uFFFDb", escapeHtml("a\uD800b", true),
                "An unpaired surrogate was written to the document");
        assertEquals("\uFFFD", escapeXml("\uD800", false),
                "A trailing unpaired surrogate was written to the document");
        assertEquals("\uFFFD\ud83d\ude00", escapeXml("\uD800\ud83d\ude00", false),
                "An unpaired surrogate ahead of a character was written to the document");
    }

    @Test
    public void serializeSupplementaryCharacterAsASingleReference() {
        final var parser = newXmlParser().shouldEncodeUTF8().shouldPrettyPrint(false).build();

        final var actual = parser.toXml(new Pojo("Mêlée \ud83d\ude00"));

        assertEquals("<pojo><name>M&#234;l&#233;e &#128512;</name></pojo>", actual, "The document holds references to surrogate code points");
        assertEquals("Mêlée \ud83d\ude00", parser.fromXml(actual, Pojo.class).name, "Round trip lost information");
    }

    @Test
    public void unescapeNamedEntitiesXml() {
        assertEquals("<>&\"'", unescapeXml("&lt;&gt;&amp;&quot;&apos;"), "Invalid named entity");
        assertEquals("&#65;", unescapeXml("&amp;#65;"), "Entity was decoded twice");
    }

    @Test
    public void unescapeMalformedXmlIsKeptAsText() {
        assertEquals("a&#x41", unescapeXml("a&#x41"), "Missing semicolon was decoded");
        assertEquals("a&#65", unescapeXml("a&#65"), "Missing semicolon was decoded");
        assertEquals("&#;", unescapeXml("&#;"), "Empty reference was decoded");
        assertEquals("&#x;", unescapeXml("&#x;"), "Empty reference was decoded");
        assertEquals("&#abc;", unescapeXml("&#abc;"), "Invalid number was decoded");
        assertEquals("&#xzz;", unescapeXml("&#xzz;"), "Invalid number was decoded");
        assertEquals("&#X41;", unescapeXml("&#X41;"), "Uppercase x was treated as hexadecimal");
        assertEquals("&#999999999999999;", unescapeXml("&#999999999999999;"), "Out of range number was decoded");
        assertEquals("&#-5;", unescapeXml("&#-5;"), "Negative number was decoded");
    }

    @Test
    public void unescapeNumericEntitiesHtml() {
        assertEquals("{&", unescapeHtml("&#123;&#x0026;"), "Invalid entity");
        assertEquals("©", unescapeHtml("&#xA9;"), "Invalid entity");
    }

    @Test
    public void unescapeMalformedHtmlIsKeptAsText() {
        assertEquals("&#;", unescapeHtml("&#;"), "Empty reference was decoded");
        assertEquals("&#x;", unescapeHtml("&#x;"), "Empty reference was decoded");
        assertEquals("&#abc;", unescapeHtml("&#abc;"), "Invalid number was decoded");
        assertEquals("&#xzz;", unescapeHtml("&#xzz;"), "Invalid number was decoded");
        assertEquals("&#123", unescapeHtml("&#123"), "Missing semicolon was decoded");
        assertEquals("&#999999999999999;", unescapeHtml("&#999999999999999;"), "Out of range number was decoded");
    }

    @Test
    public void unescapeRoundTrip() {
        final var input = "Mêlée Island \ud83d\ude00 &<>\"'";

        assertEquals(input, unescapeXml(escapeXml(input, true)), "Round trip lost information");
        assertEquals(input, unescapeHtml(escapeHtml(input, true)), "Round trip lost information");
    }

    @Test
    public void deserializeHexEntity() {
        final var parser = new XmlParser();

        final var actual = parser.fromXml("<pojo><name>&#x4A;</name></pojo>", Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("J", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void deserializeMalformedEntityKeepsText() {
        final var parser = newXmlParser().shouldPrettyPrint(false).build();

        final var actual = parser.fromXml("<pojo><name>a&#x41 b</name></pojo>", Pojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("a&#x41 b", actual.name, "Pojo has the wrong name");
    }

}
