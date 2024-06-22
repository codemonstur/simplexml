package xmlparser.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static xmlparser.utils.Constants.EMPTY;

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

    public static final Set<Character> ALL_WHITESPACE_CHARACTERS = new HashSet<>(WHITESPACE_CHARS.values());

    public static boolean isLenientWhitespace(final char c) {
        return ALL_WHITESPACE_CHARACTERS.contains(c);
    }

    // Whitespace according to the XML standard
    // https://www.w3.org/TR/xml/#sec-common-syn
    public static boolean isXmlWhitespace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

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

    public interface Trim {
        String trim(String input);
        boolean isWhitespace(char c);
    }

    public static final class NativeTrimmer implements Trim {
        public String trim(final String input) {
            return input == null ? EMPTY : input.trim();
        }
        public boolean isWhitespace(final char c) {
            return Character.isWhitespace(c);
        }
    }

    public static final class LenientTrimmer implements Trim {
        public String trim(final String input) {
            return input == null ? EMPTY : Trimming.trim(input, this::isWhitespace);
        }
        public boolean isWhitespace(final char c) {
            return isXmlWhitespace(c) || isLenientWhitespace(c);
        }
    }

    public static final class XmlTrimmer implements Trim {
        public String trim(final String input) {
            return input == null ? EMPTY : Trimming.trim(input, this::isWhitespace);
        }
        public boolean isWhitespace(final char c) {
            return isXmlWhitespace(c);
        }
    }

}
