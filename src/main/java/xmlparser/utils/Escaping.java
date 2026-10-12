package xmlparser.utils;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.Functions.isNullOrEmpty;

/**
 * Encodes the characters that would otherwise end the tag they are written in, and reads them
 * back again. Both directions are here, for XML and for HTML, and both understand numeric
 * references and characters beyond the basic plane.
 */
public enum Escaping {;

    // Returned by decodeNumericEntity for anything that isn't a usable code point
    private static final int INVALID_CODE_POINT = -1;

    /**
     * Turns text into something that can safely be written into a document. Which characters are
     * encoded, and whether characters beyond the basic plane are written as references at all, is up
     * to the implementation.
     */
    public interface Escape {
        /**
         * Turns text into something that can be written into a document; which characters that
         * are, and what becomes of the ones beyond {@code 0x7e}, is the implementation's own.
         * @param input the text to encode, which may be null
         * @param encodeUTF8 whether characters above {@code 0x7e} are written as references
         * @return the encoded text, or the input itself when it is null or empty
         */
        String escape(String input, boolean encodeUTF8);
    }

    /**
     * The other half of {@link Escape}: reads a reference, named or numeric, back into the character
     * or characters it stands for, and leaves anything it does not recognise alone.
     */
    public interface UnEscape {
        /**
         * Reads the references in a text back into the characters they stand for; what is done
         * with a reference the implementation does not know is up to it.
         * @param input the text to read back, which must not be null
         * @return the text with the references decoded
         */
        String unescape(String input);
    }

    /**
     * Encodes the five characters that would end the tag or the attribute value they sit in: the
     * two angle brackets, both quotes and the ampersand. An unpaired surrogate is not a character
     * at all and would make the document ill formed, so it is replaced with the replacement char.
     * @param str the text to encode, which may be null
     * @param encodeUTF8 whether characters above {@code 0x7e}, a surrogate pair as the one
     *                   character it is, become numeric references instead of being copied through
     * @return the encoded text, or the input itself when it is null or empty
     */
    public static String escapeXml(final String str, final boolean encodeUTF8) {
        if (isNullOrEmpty(str)) return str;

        final var encoded = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            switch (c) {
                case CHAR_LESS_THAN:
                    encoded.append(ENCODED_LESS_THAN); break;
                case CHAR_DOUBLE_QUOTE:
                    encoded.append(ENCODED_DOUBLE_QUOTE); break;
                case CHAR_GREATER_THAN:
                    encoded.append(ENCODED_GREATER_THAN); break;
                case CHAR_SINGLE_QUOTE:
                    encoded.append(ENCODED_SINGLE_QUOTE); break;
                case CHAR_AMPERSAND:
                    encoded.append(ENCODED_AMPERSAND); break;
                default:
                    i += appendCharacter(str, i, encoded, encodeUTF8) - 1;
                    break;
            }
        }

        return encoded.toString();
    }

    // Appends the character at index to the target and returns how many chars it covered. A
    // surrogate pair is a single character and is written as one, while an unpaired surrogate is
    // not a character at all and would make the document ill formed, so it is replaced.
    private static int appendCharacter(final String str, final int index, final StringBuilder target,
                                       final boolean encodeUTF8) {
        final char c = str.charAt(index);

        if (Character.isHighSurrogate(c) && index+1 < str.length() && Character.isLowSurrogate(str.charAt(index+1))) {
            final int codePoint = str.codePointAt(index);
            if (encodeUTF8) target.append(toNumericReference(codePoint));
            else target.append(str, index, index+2);
            return 2;
        }
        if (Character.isSurrogate(c)) {
            target.append(CHAR_REPLACEMENT);
            return 1;
        }
        if (encodeUTF8 && c > 0x7e) {
            target.append(toNumericReference(c));
            return 1;
        }

        target.append(c);
        return 1;
    }

    private static String toNumericReference(final int codePoint) {
        return AMPERSAND + HASH + codePoint + SEMICOLON;
    }

    /**
     * Reads the five named XML references and the numeric ones, decimal or hexadecimal, back into
     * the characters they stand for. A reference to a surrogate is joined with the partner
     * reference that follows it, and one left on its own is kept as plain text.
     * @param text the text to read back
     * @return the text with the references decoded; a reference that is not known, named or
     *         numeric, is kept as the plain text it was written as
     */
    public static String unescapeXml(final String text) {
        final var result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            final char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
            } else {
                if (text.startsWith(ENCODED_AMPERSAND, i)) {
                    result.append(CHAR_AMPERSAND);
                    i += 5;
                } else if (text.startsWith(ENCODED_SINGLE_QUOTE, i)) {
                    result.append(CHAR_SINGLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_DOUBLE_QUOTE, i)) {
                    result.append(CHAR_DOUBLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_LESS_THAN, i)) {
                    result.append(CHAR_LESS_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_GREATER_THAN, i)) {
                    result.append(CHAR_GREATER_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_UTF8, i)) {
                    final int end = appendNumericReference(text, i, result);
                    if (end == -1) {
                        // Not a valid numeric reference, so keep it as plain text
                        result.append(CHAR_AMPERSAND);
                        i++;
                    } else {
                        i = end;
                    }
                }
                else {
                    // not a reference this knows of, so it is kept as it was written rather than
                    // losing the ampersand that starts it
                    result.append(CHAR_AMPERSAND);
                    i++;
                }
            }
        }
        return result.toString();
    }

    /**
     * Encodes every character that has a named HTML entity, using the shortest name for it, and
     * then handles the rest the way {@link #escapeXml} does, replacing an unpaired surrogate with
     * the replacement character.
     * @param str the text to encode, which may be null
     * @param encodeUTF8 whether characters above {@code 0x7e}, a surrogate pair as the one
     *                   character it is, become numeric references instead of being copied through
     * @return the encoded text, or the input itself when it is null or empty
     */
    public static String escapeHtml(final String str, final boolean encodeUTF8) {
        if (isNullOrEmpty(str)) return str;

        final var encoded = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            final var entity = REVERSE_ENTITIES.get(str.charAt(i));
            if (entity != null) {
                encoded.append(entity);
                continue;
            }
            i += appendCharacter(str, i, encoded, encodeUTF8) - 1;
        }

        return encoded.toString();
    }

    /**
     * Reads the named HTML entities and the numeric ones, decimal or hexadecimal, back into the
     * characters they stand for, joining the two references of a surrogate pair into the one
     * character they make. Anything else is copied through as it was: an unknown name, a
     * reference with no ';' to close it, a number that is not a valid code point, or a surrogate
     * left without its partner.
     * @param text the text to read back
     * @return the text with the references decoded
     */
    public static String unescapeHtml(final String text) {
        final StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        while (i < text.length()) {
            final char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
                continue;
            }

            final int index = text.indexOf(';', i);
            if (index == -1) {
                result.append("&");
                i++;
                continue;
            }

            final String entity = text.substring(i, index+1);
            final String decode = NAMED_ENTITIES.get(entity);
            if (decode != null) {
                result.append(decode);
                i = index+1;
                continue;
            }

            if (text.charAt(i+1) == '#') {
                final int end = appendNumericReference(text, i, result);
                if (end == -1) {
                    // Not a valid numeric reference, so keep it as plain text
                    result.append(CHAR_AMPERSAND);
                    i++;
                    continue;
                }
                i = end;
                continue;
            }

            result.append("&");
            i++;
        }
        return result.toString();
    }

    // Appends the character that the numeric reference at start denotes, returning the index just
    // past it, or -1 when start does not hold a well formed reference. A reference to a surrogate
    // is only half a character, so it is joined with its partner here and rejected on its own.
    private static int appendNumericReference(final String text, final int start, final StringBuilder result) {
        if (!text.startsWith(ENCODED_UTF8, start)) return -1;

        final int semicolon = text.indexOf(SEMICOLON, start);
        if (semicolon == -1) return -1;

        final int codePoint = decodeNumericEntity(text, start+2, semicolon);
        if (codePoint == INVALID_CODE_POINT) return -1;

        if (isSurrogateCodePoint(codePoint))
            return appendSurrogatePair(text, semicolon+1, codePoint, result);

        // a reference to a character no document may hold would put one into it that no parser
        // reads back, so it is left as the plain text it was written as
        if (!isLegalXmlCharacter(codePoint)) return -1;

        result.appendCodePoint(codePoint);
        return semicolon+1;
    }

    // Appends the supplementary character made of the surrogate at start followed by its partner,
    // returning the index just past the pair, or -1 when the two references are not a pair.
    private static int appendSurrogatePair(final String text, final int start, final int high,
                                           final StringBuilder result) {
        if (!Character.isHighSurrogate((char) high)) return -1;
        if (!text.startsWith(ENCODED_UTF8, start)) return -1;

        final int semicolon = text.indexOf(SEMICOLON, start);
        if (semicolon == -1) return -1;

        final int low = decodeNumericEntity(text, start+2, semicolon);
        if (!isLowSurrogateCodePoint(low)) return -1;

        result.appendCodePoint(Character.toCodePoint((char) high, (char) low));
        return semicolon+1;
    }

    private static boolean isSurrogateCodePoint(final int codePoint) {
        return codePoint >= Character.MIN_SURROGATE && codePoint <= Character.MAX_SURROGATE;
    }
    private static boolean isLowSurrogateCodePoint(final int codePoint) {
        return codePoint >= Character.MIN_LOW_SURROGATE && codePoint <= Character.MAX_LOW_SURROGATE;
    }

    // Tells whether a code point is one a document may hold at all, which is what the character
    // production of XML allows: a tab, a line feed and a carriage return, then everything from a
    // space upwards that stops short of the surrogates, and everything past them up to the end of
    // the last plane. The character just below the surrogate block and the two at the end of the
    // basic plane are left out, being unassigned, and everything else outside the ranges would
    // make the document ill formed.
    private static boolean isLegalXmlCharacter(final int codePoint) {
        return codePoint == 0x9 || codePoint == 0xA || codePoint == 0xD
                || (codePoint >= 0x20 && codePoint < Character.MIN_SURROGATE)
                || (codePoint > Character.MAX_SURROGATE && codePoint <= 0xFFFD)
                || (codePoint >= 0x10000 && codePoint <= Character.MAX_CODE_POINT);
    }

    // Decodes the body of a numeric character reference, which is either decimal or, when it is
    // prefixed with an x, hexadecimal. Returns INVALID_CODE_POINT when the body is not a valid
    // number or not a valid code point; the callers keep such a reference as plain text. The
    // body is checked one character at a time rather than left to Integer.parseInt, which takes
    // a leading sign, a space around the digits and a few other spellings that no document
    // writes in a reference.
    private static int decodeNumericEntity(final String text, final int start, final int end) {
        if (start > end) return INVALID_CODE_POINT;

        final boolean isHex = start < end && text.charAt(start) == 'x';
        final String body = isHex ? text.substring(start+1, end) : text.substring(start, end);
        if (body.isEmpty()) return INVALID_CODE_POINT;

        for (int i = 0; i < body.length(); i++)
            if (!isDigit(body.charAt(i), isHex)) return INVALID_CODE_POINT;

        try {
            final int codePoint = Integer.parseInt(body, isHex ? 16 : 10);
            return Character.isValidCodePoint(codePoint) ? codePoint : INVALID_CODE_POINT;
        } catch (final NumberFormatException e) {
            return INVALID_CODE_POINT;
        }
    }

    // Tells whether a character is one the body of a numeric reference may hold: a digit, and a
    // letter from a to f in either case when the number is hexadecimal.
    private static boolean isDigit(final char c, final boolean isHex) {
        if (c >= '0' && c <= '9') return true;
        if (!isHex) return false;
        return c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

}
