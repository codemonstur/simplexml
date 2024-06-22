package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.utils.Trimming;
import xmlparser.utils.Trimming.Trim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class TrimmingTest {

    @Test
    public void trimXmlWhiteSpace() {
        final var parser = newXmlParser().strictWhitespace().build();
        final var input = "<pojo><name>\r Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test
    public void dontTrimNonXmlWhiteSpace() {
        final var parser = newXmlParser().strictWhitespace().build();
        final var input = "<pojo><name>\r\u2004 Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "\u2004 Hello", actual.name);
    }

    @Test
    public void trimNativeWhiteSpace() {
        final var parser = newXmlParser().build();
        final var input = "<pojo><name>\r\u000f Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test
    public void dontTrimNonNativeWhiteSpace() {
        final var parser = newXmlParser().build();
        final var input = "<pojo><name>\r\u2004 Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "\u2004 Hello", actual.name);
    }

    @Test
    public void trimLenientWhiteSpace() {
        final var parser = newXmlParser().lenientWhitespace().build();
        final var input = "<pojo><name>\r\u2004 Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test
    public void trimCustomWhiteSpace() {
        final var parser = newXmlParser().trimmer(new Trim() {
            public String trim(final String input) {
                return Trimming.trim(input, this::isWhitespace);
            }
            public boolean isWhitespace(final char c) {
                return c == '\t' || c == ' ' || c == '\n';
            }
        }).build();
        final var input = "<pojo><name>\t Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test
    public void dontTrimNonCustomWhiteSpace() {
        final var parser = newXmlParser().trimmer(new Trim() {
            public String trim(final String input) {
                return Trimming.trim(input, this::isWhitespace);
            }
            public boolean isWhitespace(final char c) {
                return c == '\t' || c == ' ' || c == '\n';
            }
        }).build();
        final var input = "<pojo><name>\t\r Hello \t\n</name></pojo>";

        final var actual = parser.fromXml(input, Pojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "\r Hello", actual.name);
    }

}
