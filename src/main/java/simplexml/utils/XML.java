package simplexml.utils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static simplexml.utils.Constants.*;
import static simplexml.utils.Functions.isNullOrEmpty;
import static simplexml.utils.Reflection.toName;

public enum XML {;

    public static String escapeXml(final String str, final boolean encodeUTF8) {
        if (isNullOrEmpty(str)) return str;

        final StringBuffer encoded = new StringBuffer();
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
        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char charAt = text.charAt(i);
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
                } else i++;
            }
        }
        return result.toString();
    }

    public static String attributesToXml(final List<Field> fields, final Object o, final boolean shouldEncodeUTF8) throws IllegalArgumentException,
            IllegalAccessException {
        final StringBuilder attr = new StringBuilder(6*fields.size());
        for (final Field f : fields) {
            addAttribute(attr, toName(f), escapeXml(f.get(o).toString(), shouldEncodeUTF8));
        }
        return attr.toString();
    }
    public static String attributesToXml(final Map<String, String> map, final boolean shouldEncodeUTF8) {
        if (map == null || map.isEmpty()) return EMPTY;

        final StringBuilder builder = new StringBuilder();
        for (final Entry<String, String> entry : map.entrySet()) {
            addAttribute(builder, entry.getKey(), escapeXml(entry.getValue(), shouldEncodeUTF8));
        }
        return builder.toString();
    }
    public static void addAttribute(final StringBuilder builder, final String name, final String value) {
        builder.append(SPACE).append(name).append(EQUALS).append(DOUBLE_QUOTE).append(value).append(DOUBLE_QUOTE);
    }

    public static void writeTag(final Writer writer, final String name, final String text) throws IOException {
        if (isNullOrEmpty(text)) {
            writeSelfClosingTag(writer, name);
        } else {
            writeOpeningAndClosingTag(writer, name, text);
        }
    }
    public static void writeTag(final Writer writer, final String name, final String attributes, final String text) throws IOException {
        if (isNullOrEmpty(text)) {
            writeSelfClosingTag(writer, name, attributes);
        } else {
            writeOpeningTag(writer, name, attributes);
            writer.append(text);
            writeClosingTag(writer, name);
        }
    }
    public static void writeOpeningAndClosingTag(final Writer writer, final String name, final String text) throws IOException {
        writeOpeningTag(writer, name);
        writer.append(text);
        writeClosingTag(writer, name);
    }
    public static void writeOpeningTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(name).append(GREATER_THAN);
    }
    public static void writeOpeningTag(final Writer writer, final String name, final String attributes) throws IOException {
        writer.append(LESS_THAN).append(name).append(attributes).append(GREATER_THAN);
    }
    public static void writeClosingTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(FORWARD_SLASH).append(name).append(GREATER_THAN);
    }
    public static void writeSelfClosingTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(name).append(FORWARD_SLASH).append(GREATER_THAN);
    }
    public static void writeSelfClosingTag(final Writer writer, final String name, final String attributes) throws IOException {
        writer.append(LESS_THAN).append(name).append(attributes).append(SPACE).append(FORWARD_SLASH).append(GREATER_THAN);
    }

}
