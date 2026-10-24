package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.utils.XML;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagNameTest {

    @Test
    public void plainNames() {
        assertValid("a", "_x", ":x", "ns:elem", "my-tag", "a.b", "x1", "a-b.c:d_1");
    }

    @Test
    public void namesOutsideTheAsciiRange() {
        assertValid("é", "π", "Д");
    }

    @Test
    public void namesHoldingASupplementaryCharacter() {
        // a name may hold a character beyond the basic multilingual plane, which is two chars long
        assertValid("\uD83D\uDE00x", "x\uD83D\uDE00");
    }

    @Test
    public void namesThatHoldSomethingElse() {
        assertInvalid("", "a<b", "x&y", "a=b", "a b", "a\"b", "a>b", "a/>", "a'b", "\t");
    }

    @Test
    public void namesThatDoNotStartWithANameStartChar() {
        assertInvalid("1a", "-1", ".1", "  a", "\n", " ", null);
    }

    private void assertValid(final String... names) {
        for (final String name : names)
            assertTrue(XML.isValidTagName(name), "'" + name + "' should be a valid tag name");
    }

    private void assertInvalid(final String... names) {
        for (final String name : names)
            assertFalse(XML.isValidTagName(name), "'" + name + "' should not be a valid tag name");
    }

}
