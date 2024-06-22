package xmlparser.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.Functions.isNullOrEmpty;

public enum Escaping {;

    public interface Escape {
        String escape(String input, boolean encodeUTF8);
    }
    public interface UnEscape {
        String unescape(String input);
    }

    public static String escapeXml(final String str, final boolean encodeUTF8) {
        if (isNullOrEmpty(str)) return str;

        final var encoded = new StringBuilder();
        for (final char c : str.toCharArray()) {
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
                    encoded.append( (encodeUTF8 && c > 0x7e) ? AMPERSAND+HASH+((int)c)+SEMICOLON : c);
                    break;
            }
        }

        return encoded.toString();
    }

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
                    final int index = text.indexOf(';', i);
                    result.append(charFromDecimal(text.substring(i+2, index)));
                    i = index+1;
                }
                else i++;
            }
        }
        return result.toString();
    }

    public static String escapeHtml(final String str, final boolean encodeUTF8) {
        if (isNullOrEmpty(str)) return str;

        final var encoded = new StringBuilder();
        for (final char c : str.toCharArray()) {
            final var entity = REVERSE_ENTITIES.get(c);
            if (entity != null) {
                encoded.append(entity);
                continue;
            }
            if (encodeUTF8 && c > 0x7e) {
                encoded.append(AMPERSAND+HASH+((int)c)+SEMICOLON);
                continue;
            }

            encoded.append(c);
        }

        return encoded.toString();
    }

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
                final char real = text.charAt(i+2) == 'x'
                        ? charFromHex(text.substring(i+3, index))
                        : charFromDecimal(text.substring(i+2, index));
                result.append(real);
                i = index+1;
                continue;
            }

            result.append("&");
            i++;
        }
        return result.toString();
    }

    private static char charFromDecimal(final String substring) {
        return (char) Integer.parseInt(substring);
    }
    private static char charFromHex(final String substring) {
        return (char) Integer.parseInt(substring, 16);
    }

}
