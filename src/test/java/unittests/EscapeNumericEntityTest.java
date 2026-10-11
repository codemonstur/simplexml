package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.utils.Escaping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class EscapeNumericEntityTest {

    @Test
    public void unescapeDecimalXml() {
        assertEquals("Invalid decimal entity", "A", Escaping.unescapeXml("&#65;"));
        assertEquals("Invalid decimal entity", "©{&", Escaping.unescapeXml("&#169;&#123;&#38;"));
    }

    @Test
    public void unescapeHexXml() {
        assertEquals("Invalid hexadecimal entity", "A", Escaping.unescapeXml("&#x41;"));
        assertEquals("Invalid hexadecimal entity", "J", Escaping.unescapeXml("&#x4a;"));
        assertEquals("Invalid hexadecimal entity", "&", Escaping.unescapeXml("&#x0026;"));
    }

    @Test
    public void unescapeCodePointsBeyondBmpXml() {
        assertEquals("Invalid code point", "\ud83d\ude00", Escaping.unescapeXml("&#128512;"));
        assertEquals("Invalid code point", "\ud83d\ude00", Escaping.unescapeXml("&#x1f600;"));
    }

    @Test
    public void unescapeSurrogateReferencesAreJoinedIntoOneCharacter() {
        // A supplementary character used to be escaped as two references, one to each half
        assertEquals("Surrogate references were not joined", "\ud83d\ude00",
                Escaping.unescapeXml("&#55357;&#56832;"));
        assertEquals("Surrogate references were not joined", "\ud83d\ude00",
                Escaping.unescapeXml("&#xD83D;&#xDE00;"));
        assertEquals("Surrogate references were not joined", "\ud83d\ude00",
                Escaping.unescapeHtml("&#55357;&#56832;"));
    }

    @Test
    public void unescapeLoneSurrogateReferenceIsKeptAsText() {
        assertEquals("A surrogate without its partner was decoded", "&#55296;", Escaping.unescapeXml("&#55296;"));
        assertEquals("A surrogate without its partner was decoded", "&#56320;", Escaping.unescapeXml("&#56320;"));
        assertEquals("A high surrogate was joined to a character that is not its partner", "&#55357;A",
                Escaping.unescapeXml("&#55357;&#65;"));
        assertEquals("A surrogate without its partner was decoded", "&#55296;", Escaping.unescapeHtml("&#55296;"));
    }

    @Test
    public void escapeSupplementaryCharacterIsASingleReference() {
        assertEquals("A surrogate pair was escaped as a reference to each half", "&#128512;",
                Escaping.escapeXml("\ud83d\ude00", true));
        assertEquals("A surrogate pair was escaped as a reference to each half", "&#128512;",
                Escaping.escapeHtml("\ud83d\ude00", true));
        assertEquals("A surrogate pair was changed", "\ud83d\ude00",
                Escaping.escapeXml("\ud83d\ude00", false));
    }

    @Test
    public void escapeLoneSurrogateIsReplaced() {
        assertEquals("An unpaired surrogate was written to the document", "a\uFFFDb",
                Escaping.escapeXml("a\uD800b", false));
        assertEquals("An unpaired surrogate was written to the document", "a\uFFFDb",
                Escaping.escapeXml("a\uD800b", true));
        assertEquals("An unpaired surrogate was written to the document", "a\uFFFDb",
                Escaping.escapeXml("a\uDC00b", false));
        assertEquals("An unpaired surrogate was written to the document", "a\uFFFDb",
                Escaping.escapeHtml("a\uD800b", true));
        assertEquals("A trailing unpaired surrogate was written to the document", "\uFFFD",
                Escaping.escapeXml("\uD800", false));
        assertEquals("An unpaired surrogate ahead of a character was written to the document", "\uFFFD\ud83d\ude00",
                Escaping.escapeXml("\uD800\ud83d\ude00", false));
    }

    @Test
    public void serializeSupplementaryCharacterAsASingleReference() {
        final var parser = newXmlParser().shouldEncodeUTF8().shouldPrettyPrint(false).build();

        final var actual = parser.toXml(new Pojo("Mêlée \ud83d\ude00"));

        assertEquals("The document holds references to surrogate code points",
                "<pojo><name>M&#234;l&#233;e &#128512;</name></pojo>", actual);
        assertEquals("Round trip lost information", "Mêlée \ud83d\ude00",
                parser.fromXml(actual, Pojo.class).name);
    }

    @Test
    public void unescapeNamedEntitiesXml() {
        assertEquals("Invalid named entity", "<>&\"'", Escaping.unescapeXml("&lt;&gt;&amp;&quot;&apos;"));
        assertEquals("Entity was decoded twice", "&#65;", Escaping.unescapeXml("&amp;#65;"));
    }

    @Test
    public void unescapeMalformedXmlIsKeptAsText() {
        assertEquals("Missing semicolon was decoded", "a&#x41", Escaping.unescapeXml("a&#x41"));
        assertEquals("Missing semicolon was decoded", "a&#65", Escaping.unescapeXml("a&#65"));
        assertEquals("Empty reference was decoded", "&#;", Escaping.unescapeXml("&#;"));
        assertEquals("Empty reference was decoded", "&#x;", Escaping.unescapeXml("&#x;"));
        assertEquals("Invalid number was decoded", "&#abc;", Escaping.unescapeXml("&#abc;"));
        assertEquals("Invalid number was decoded", "&#xzz;", Escaping.unescapeXml("&#xzz;"));
        assertEquals("Uppercase x was treated as hexadecimal", "&#X41;", Escaping.unescapeXml("&#X41;"));
        assertEquals("Out of range number was decoded", "&#999999999999999;", Escaping.unescapeXml("&#999999999999999;"));
        assertEquals("Negative number was decoded", "&#-5;", Escaping.unescapeXml("&#-5;"));
    }

    @Test
    public void unescapeNumericEntitiesHtml() {
        assertEquals("Invalid entity", "{&", Escaping.unescapeHtml("&#123;&#x0026;"));
        assertEquals("Invalid entity", "©", Escaping.unescapeHtml("&#xA9;"));
    }

    @Test
    public void unescapeMalformedHtmlIsKeptAsText() {
        assertEquals("Empty reference was decoded", "&#;", Escaping.unescapeHtml("&#;"));
        assertEquals("Empty reference was decoded", "&#x;", Escaping.unescapeHtml("&#x;"));
        assertEquals("Invalid number was decoded", "&#abc;", Escaping.unescapeHtml("&#abc;"));
        assertEquals("Invalid number was decoded", "&#xzz;", Escaping.unescapeHtml("&#xzz;"));
        assertEquals("Missing semicolon was decoded", "&#123", Escaping.unescapeHtml("&#123"));
        assertEquals("Out of range number was decoded", "&#999999999999999;", Escaping.unescapeHtml("&#999999999999999;"));
    }

    @Test
    public void unescapeRoundTrip() {
        final var input = "Mêlée Island \ud83d\ude00 &<>\"'";

        assertEquals("Round trip lost information", input, Escaping.unescapeXml(Escaping.escapeXml(input, true)));
        assertEquals("Round trip lost information", input, Escaping.unescapeHtml(Escaping.escapeHtml(input, true)));
    }

    @Test
    public void deserializeHexEntity() {
        final var parser = new XmlParser();

        final var actual = parser.fromXml("<pojo><name>&#x4A;</name></pojo>", Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "J", actual.name);
    }

    @Test
    public void deserializeMalformedEntityKeepsText() {
        final var parser = newXmlParser().shouldPrettyPrint(false).build();

        final var actual = parser.fromXml("<pojo><name>a&#x41 b</name></pojo>", Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "a&#x41 b", actual.name);
    }

}
