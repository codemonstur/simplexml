package xmlparser.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static xmlparser.utils.Constants.EMPTY;

/**
 * Decides what whitespace is, and offers three opinions on it. Four characters are whitespace by
 * the XML standard, but a great many more look like it and turn up in documents from the wild, so
 * which of those counts is left to the parser to be told.
 */
public enum Trimming {;

    // Retrieved from various sources, none of these sources have the complete list.
    // The list may still miss some white space characters and some that are included
    // can be arguably removed.
    //
    // I made the list as extensive and inclusive as I could to allow for as many
    // whitespace characters as seemed reasonable.
    //
    // http://jkorpela.fi/chars/spaces.html
    // https://en.wikipedia.org/wiki/Non-breaking_space
    // https://en.wikipedia.org/wiki/Unicode_control_characters
    // https://unicode-search.net/unicode-namesearch.pl?term=SEPARATOR
    // https://stackoverflow.com/questions/28295504/how-to-trim-no-break-space-in-java
    // https://www.fileformat.info/info/unicode/category/Zs/list.htm
    /**
     * Every character the lenient opinion counts as whitespace, each one keyed by a readable
     * name: the control characters, the exotic spaces and the other look-alikes, on top of the
     * four the XML standard allows.
     */
    public static Map<String, Character> WHITESPACE_CHARS = ofEntries(
        entry("control character 0000", '\u0000'), entry("control character 0001", '\u0001'),
        entry("control character 0002", '\u0002'), entry("control character 0003", '\u0003'),
        entry("control character 0004", '\u0004'), entry("control character 0005", '\u0005'),
        entry("control character 0006", '\u0006'), entry("control character 0007", '\u0007'),
        entry("control character 0008", '\u0008'), entry("tab", '\t'),
        entry("line feed", '\n'), entry("control character 000b", '\u000b'),
        entry("control character 000c", '\u000c'), entry("carriage return", '\r'),
        entry("control character 000e", '\u000e'), entry("control character 000f", '\u000f'),
        entry("control character 0010", '\u0010'), entry("control character 0011", '\u0011'),
        entry("control character 0012", '\u0012'), entry("control character 0013", '\u0013'),
        entry("control character 0014", '\u0014'), entry("control character 0015", '\u0015'),
        entry("control character 0016", '\u0016'), entry("control character 0017", '\u0017'),
        entry("control character 0018", '\u0018'), entry("control character 0019", '\u0019'),
        entry("control character 001a", '\u001a'), entry("control character 001b", '\u001b'),
        entry("information separator four", '\u001c'), entry("information separator three", '\u001d'),
        entry("information separator two", '\u001e'), entry("information separator one", '\u001f'),
        entry("space", '\u0020'), entry("no-break space", '\u00a0'),
        entry("control character 0080", '\u0080'), entry("control character 0081", '\u0081'),
        entry("control character 0082", '\u0082'), entry("control character 0083", '\u0083'),
        entry("control character 0084", '\u0084'), entry("control character 0085", '\u0085'),
        entry("control character 0086", '\u0086'), entry("control character 0087", '\u0087'),
        entry("control character 0088", '\u0088'), entry("control character 0089", '\u0089'),
        entry("control character 008a", '\u008a'), entry("control character 008b", '\u008b'),
        entry("control character 008c", '\u008c'), entry("control character 008d", '\u008d'),
        entry("control character 008e", '\u008e'), entry("control character 008f", '\u008f'),
        entry("control character 0090", '\u0090'), entry("control character 0091", '\u0091'),
        entry("control character 0092", '\u0092'), entry("control character 0093", '\u0093'),
        entry("control character 0094", '\u0094'), entry("control character 0095", '\u0095'),
        entry("control character 0096", '\u0096'), entry("control character 0097", '\u0097'),
        entry("control character 0098", '\u0098'), entry("control character 0099", '\u0099'),
        entry("control character 009a", '\u009a'), entry("control character 009b", '\u009b'),
        entry("control character 009c", '\u009c'), entry("control character 009d", '\u009d'),
        entry("control character 009e", '\u009e'), entry("control character 009f", '\u009f'),
        entry("ogham space mark", '\u1680'), entry("mongolian vowel separator", '\u180e'),
        entry("en quad", '\u2000'), entry("em quad", '\u2001'), entry("en space", '\u2002'),
        entry("em space", '\u2003'), entry("three-per-em space", '\u2004'),
        entry("four-per-em space", '\u2005'), entry("six-per-em space", '\u2006'),
        entry("figure space", '\u2007'), entry("punctuation space", '\u2008'),
        entry("thin space", '\u2009'), entry("hair space", '\u200a'),
        entry("zero width space", '\u200b'), entry("line separator", '\u2028'),
        entry("paragraph separator", '\u2029'), entry("narrow no-break space", '\u202f'),
        entry("medium mathematical space", '\u205f'), entry("word joiner", '\u2060'),
        entry("invisible separator", '\u2063'), entry("ideographic space", '\u3000'),
        entry("zero width no-break space", '\ufeff'), entry("anchor fff9", '\ufff9'),
        entry("interlinear annotation separator", '\ufffa'), entry("terminator fffb", '\ufffb')
    );

    /**
     * The same characters as {@link #WHITESPACE_CHARS} without their names, for quick lookups.
     */
    public static final Set<Character> ALL_WHITESPACE_CHARACTERS = new HashSet<>(WHITESPACE_CHARS.values());

    /**
     * Tells whether a character is whitespace under the lenient opinion, which is every character
     * of {@link #ALL_WHITESPACE_CHARACTERS}.
     * @param c the character to test
     * @return true if a lenient trimmer would strip it
     */
    public static boolean isLenientWhitespace(final char c) {
        return ALL_WHITESPACE_CHARACTERS.contains(c);
    }

    // Whitespace according to the XML standard
    // https://www.w3.org/TR/xml/#sec-common-syn
    /**
     * Tells whether a character is whitespace according to the XML standard, which allows four
     * of them and no others.
     * @param c the character to test
     * @return true for space, tab, line feed and carriage return
     */
    public static boolean isXmlWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    /**
     * Strips whitespace from both ends of a string using the caller's own opinion of which
     * characters count.
     * @param value the string to strip
     * @param isWhitespace decides which characters count as whitespace
     * @return the stripped string, or the given string itself when nothing had to come off
     */
    public static String trim(final String value, final Function<Character, Boolean> isWhitespace) {
        final char[] val = value.toCharArray();

        int start = 0;
        int end = value.length();
        while (start < end && isWhitespace.apply(val[start])) {
            start++;
        }
        while (start < end && isWhitespace.apply(val[end - 1])) {
            end--;
        }

        return ((start > 0) || (end < value.length())) ? value.substring(start, end) : value;
    }

    /**
     * One opinion about whitespace: what counts as it, and how to strip it from the ends of a string.
     * The two halves have to agree, or a character that one call calls whitespace is one the other
     * will leave behind.
     */
    public interface Trim {
        /**
         * Removes whitespace from both ends of a string; a null input comes back as an empty
         * string rather than as null.
         * @param input the string to strip, which may be null
         * @return the stripped string
         */
        String trim(String input);
        /**
         * Says whether this opinion counts a character as whitespace, which is what decides what
         * gets stripped from the ends.
         * @param c the character to test
         * @return true if the character counts as whitespace here
         */
        boolean isWhitespace(char c);
    }

    /**
     * Uses the JDK's own idea of what whitespace is: Unicode aware, and not the same set as the XML
     * standard. This is what a parser built without saying otherwise uses.
     */
    public static final class NativeTrimmer implements Trim {
        /** Trims with the JDK's own idea of whitespace. */
        public NativeTrimmer() { }
        public String trim(final String input) {
            return input == null ? EMPTY : input.trim();
        }
        public boolean isWhitespace(final char c) {
            return Character.isWhitespace(c);
        }
    }

    /**
     * Trims the four characters the XML standard allows, plus every other character that has any
     * business looking like whitespace: exotic spaces, control characters and the like.
     */
    public static final class LenientTrimmer implements Trim {
        /** Trims with the lenient opinion: XML whitespace plus everything that looks like it. */
        public LenientTrimmer() { }
        public String trim(final String input) {
            return input == null ? EMPTY : Trimming.trim(input, this::isWhitespace);
        }
        public boolean isWhitespace(final char c) {
            return isXmlWhitespace(c) || isLenientWhitespace(c);
        }
    }

    /**
     * Trims exactly the four characters the XML standard calls whitespace, space, tab, line feed and
     * carriage return, and nothing else.
     */
    public static final class XmlTrimmer implements Trim {
        /** Trims with the XML standard's four whitespace characters and nothing else. */
        public XmlTrimmer() { }
        public String trim(final String input) {
            return input == null ? EMPTY : Trimming.trim(input, this::isWhitespace);
        }
        public boolean isWhitespace(final char c) {
            return isXmlWhitespace(c);
        }
    }

}
