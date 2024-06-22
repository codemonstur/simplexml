package xmlparser.utils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.Functions.isNullOrEmpty;
import static xmlparser.utils.Reflection.toName;

public enum XML {;

    public static String attributesToXml(final List<Field> fields, final Object o, final Function<String, String> escaper) throws IllegalArgumentException,
            IllegalAccessException {
        final StringBuilder attr = new StringBuilder(6*fields.size());
        for (final Field f : fields) {
            addAttribute(attr, toName(f), escaper.apply(f.get(o).toString()));
        }
        return attr.toString();
    }
    public static String attributesToXml(final Map<String, String> map, final Function<String, String> escaper) {
        if (map == null || map.isEmpty()) return EMPTY;

        final StringBuilder builder = new StringBuilder();
        for (final Entry<String, String> entry : map.entrySet()) {
            addAttribute(builder, entry.getKey(), escaper.apply(entry.getValue()));
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
