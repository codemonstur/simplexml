package xmlparser.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
    public static Map<String, Character> WHITESPACE_CHARS = Builder.<String, Character>newHashMap()
        .put("control character 0000", '\u0000')
        .put("control character 0001", '\u0001')
        .put("control character 0002", '\u0002')
        .put("control character 0003", '\u0003')
        .put("control character 0004", '\u0004')
        .put("control character 0005", '\u0005')
        .put("control character 0006", '\u0006')
        .put("control character 0007", '\u0007')
        .put("control character 0008", '\u0008')
        .put("line feed", '\n')
        .put("control character 000b", '\u000b')
        .put("control character 000c", '\u000c')
        .put("carriage return", '\r')
        .put("control character 000e", '\u000e')
        .put("control character 000f", '\u000f')
        .put("control character 0010", '\u0010')
        .put("control character 0011", '\u0011')
        .put("control character 0012", '\u0012')
        .put("control character 0013", '\u0013')
        .put("control character 0014", '\u0014')
        .put("control character 0015", '\u0015')
        .put("control character 0016", '\u0016')
        .put("control character 0017", '\u0017')
        .put("control character 0018", '\u0018')
        .put("control character 0019", '\u0019')
        .put("control character 001a", '\u001a')
        .put("control character 001b", '\u001b')
        .put("information separator four", '\u001c')
        .put("information separator three", '\u001d')
        .put("information separator two", '\u001e')
        .put("information separator one", '\u001f')
        .put("space", '\u0020')
        .put("no-break space", '\u00a0')
        .put("control character 0080", '\u0080')
        .put("control character 0081", '\u0081')
        .put("control character 0082", '\u0082')
        .put("control character 0083", '\u0083')
        .put("control character 0084", '\u0084')
        .put("control character 0085", '\u0085')
        .put("control character 0086", '\u0086')
        .put("control character 0087", '\u0087')
        .put("control character 0088", '\u0088')
        .put("control character 0089", '\u0089')
        .put("control character 008a", '\u008a')
        .put("control character 008b", '\u008b')
        .put("control character 008c", '\u008c')
        .put("control character 008d", '\u008d')
        .put("control character 008e", '\u008e')
        .put("control character 008f", '\u008f')
        .put("control character 0090", '\u0090')
        .put("control character 0091", '\u0091')
        .put("control character 0092", '\u0092')
        .put("control character 0093", '\u0093')
        .put("control character 0094", '\u0094')
        .put("control character 0095", '\u0095')
        .put("control character 0096", '\u0096')
        .put("control character 0097", '\u0097')
        .put("control character 0098", '\u0098')
        .put("control character 0099", '\u0099')
        .put("control character 009a", '\u009a')
        .put("control character 009b", '\u009b')
        .put("control character 009c", '\u009c')
        .put("control character 009d", '\u009d')
        .put("control character 009e", '\u009e')
        .put("control character 009f", '\u009f')
        .put("ogham space mark", '\u1680')
        .put("mongolian vowel separator", '\u180e')
        .put("en quad", '\u2000')
        .put("em quad", '\u2001')
        .put("en space", '\u2002')
        .put("em space", '\u2003')
        .put("three-per-em space", '\u2004')
        .put("four-per-em space", '\u2005')
        .put("six-per-em space", '\u2006')
        .put("figure space", '\u2007')
        .put("punctuation space", '\u2008')
        .put("thin space", '\u2009')
        .put("hair space", '\u200a')
        .put("zero width space", '\u200b')
        .put("line separator", '\u2028')
        .put("paragraph separator", '\u2029')
        .put("narrow no-break space", '\u202f')
        .put("medium mathematical space", '\u205f')
        .put("word joiner", '\u2060')
        .put("invisible separator", '\u2063')
        .put("ideographic space", '\u3000')
        .put("tab", '\t')
        .put("zero width no-break space", '\ufeff')
        .put("anchor fff9", '\ufff9')
        .put("interlinear annotation separator", '\ufffa')
        .put("terminator fffb", '\ufffb')
        .build();

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
            return input == null ? EMPTY : Trimming.trim(input, Trimming::isLenientWhitespace);
        }
        public boolean isWhitespace(final char c) {
            return isXmlWhitespace(c) || isLenientWhitespace(c);
        }
    }

    public static final class XmlTrimmer implements Trim {
        public String trim(final String input) {
            return input == null ? EMPTY : Trimming.trim(input, Trimming::isXmlWhitespace);
        }
        public boolean isWhitespace(final char c) {
            return isXmlWhitespace(c);
        }
    }

}
