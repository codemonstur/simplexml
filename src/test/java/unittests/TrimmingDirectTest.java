package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.utils.Trimming;
import xmlparser.utils.Trimming.Trim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrimmingDirectTest {

    @Test
    public void trimStripsBothEnds() {
        assertEquals("x", Trimming.trim("  x  ", c -> c == ' '));
    }

    @Test
    public void trimNothingWhenNoWhitespace() {
        assertEquals("x", Trimming.trim("x", c -> c == ' '));
    }

    @Test
    public void trimAllWhitespaceToEmpty() {
        assertEquals("", Trimming.trim("   ", c -> c == ' '));
    }

    @Test
    public void trimNullHandlingInTrimmers() {
        assertEquals("", new Trimming.NativeTrimmer().trim(null));
        assertEquals("", new Trimming.XmlTrimmer().trim(null));
        assertEquals("", new Trimming.LenientTrimmer().trim(null));
    }

    @Test
    public void trimTrimmerConsistency() {
        final var t = new Trimming.XmlTrimmer();
        assertEquals("x", t.trim("  x  "));
        assertTrue(t.isWhitespace(' '));
        assertFalse(t.isWhitespace('x'));
    }

    @Test
    public void lenientTrimmerIsWhitespace() {
        final var t = new Trimming.LenientTrimmer();
        assertTrue(t.isWhitespace('\u00a0'));
        assertTrue(t.isWhitespace(' '));
    }

    @Test
    public void xmlTrimmerOnlyFour() {
        final var t = new Trimming.XmlTrimmer();
        assertTrue(t.isWhitespace(' '));
        assertFalse(t.isWhitespace('\u00a0'));
    }
}
