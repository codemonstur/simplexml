package xmlparser.utils;

import xmlparser.annotations.XmlDefaultValue;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.Functions.isNullOrEmpty;
import static xmlparser.utils.Reflection.toName;

/**
 * Writes the pieces of XML that turn up again and again: a tag, the attributes on it, the text
 * inside it, and a whole element with everything under it. Serialization comes through here so
 * that the same rules apply whether the source is an object or a DOM tree.
 */
public enum XML {;

    /**
     * Renders the attribute fields of an object as a run of name="value" pairs, each value passed
     * through the escaper. A field holding null falls back to the value of its @XmlDefaultValue.
     * @param fields the attribute fields to render
     * @param o the object to read the values from
     * @param escaper applied to each value before it is written
     * @return the attributes as text, ready to sit just inside a tag
     * @throws IllegalArgumentException if the field is not declared by the object's class
     * @throws IllegalAccessException if a field cannot be read
     */
    public static String attributesToXml(final List<Field> fields, final Object o
            , final Function<String, String> escaper) throws IllegalArgumentException, IllegalAccessException {
        return attributesToXml(fields, o, escaper, Object::toString);
    }
    /**
     * Renders the attribute fields of an object as a run of name="value" pairs, each value passed
     * through the escaper. A field holding null falls back to the value of its @XmlDefaultValue.
     * The renderer turns a value into the text it is written as, so that an enum is written as its
     * @XmlEnumValue and a type with a serializer as that serializer's text rather than as toString.
     * @param fields the attribute fields to render
     * @param o the object to read the values from
     * @param escaper applied to each value before it is written
     * @param renderer turns a value into the text it is written as
     * @return the attributes as text, ready to sit just inside a tag
     * @throws IllegalArgumentException if the field is not declared by the object's class
     * @throws IllegalAccessException if a field cannot be read
     */
    public static String attributesToXml(final List<Field> fields, final Object o
            , final Function<String, String> escaper, final Function<Object, String> renderer)
            throws IllegalArgumentException, IllegalAccessException {
        final var attr = new StringBuilder(6 * fields.size());
        for (final var field : fields) {
            // a null field carries @XmlDefaultValue(serialize=true), so use it instead of the value
            addAttribute(attr, toName(field), escaper.apply(renderer.apply(toValue(field, o))));
        }
        return attr.toString();
    }
    /**
     * Renders already named attributes as a run of name="value" pairs, each value passed through
     * the escaper. A map that is null or empty renders as nothing at all.
     * @param map the attributes to render, or null for none
     * @param escaper applied to each value before it is written
     * @return the attributes as text, ready to sit just inside a tag
     */
    public static String attributesToXml(final Map<String, String> map, final Function<String, String> escaper) {
        if (map == null || map.isEmpty()) return EMPTY;

        final var builder = new StringBuilder();
        for (final var entry : map.entrySet()) {
            addAttribute(builder, entry.getKey(), escaper.apply(entry.getValue()));
        }
        return builder.toString();
    }
    /**
     * Appends one name="value" pair to a builder, the value written between double quotes. A null
     * value is written as an empty one, and the name is written exactly as given.
     * @param builder the builder to append to
     * @param name the name of the attribute
     * @param value the value of the attribute, or null for an empty one
     */
    public static void addAttribute(final StringBuilder builder, final String name, final String value) {
        builder.append(SPACE).append(name).append(EQUALS).append(DOUBLE_QUOTE)
                .append(value == null ? EMPTY : value).append(DOUBLE_QUOTE);
    }
    /**
     * Tells whether a string is usable as an XML name, which is what a map key has to be when it
     * is written as a tag. A name starts with a letter, '_' or ':' and carries on with those plus
     * '-', '.', digits and a few combining marks. A key holding a '&lt;' or an '&amp;' is not a
     * name at all, and there is no way to escape it into one, so it is rejected instead of being
     * written.
     * @param name the string to check
     * @return true if the string can stand as a tag name
     */
    public static boolean isValidTagName(final String name) {
        if (isNullOrEmpty(name)) return false;

        final int start = name.codePointAt(0);
        if (!isNameStartChar(start)) return false;

        for (int i = Character.charCount(start); i < name.length(); ) {
            final int codePoint = name.codePointAt(i);
            if (!isNameChar(codePoint)) return false;
            i += Character.charCount(codePoint);
        }
        return true;
    }

    // The NameStartChar production of the XML specification.
    private static boolean isNameStartChar(final int c) {
        return c == ':' || c == '_'
            || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z'
            || c >= 0xC0 && c <= 0xD6
            || c >= 0xD8 && c <= 0xF6
            || c >= 0xF8 && c <= 0x2FF
            || c >= 0x370 && c <= 0x37D
            || c >= 0x37F && c <= 0x1FFF
            || c >= 0x200C && c <= 0x200D
            || c >= 0x2070 && c <= 0x218F
            || c >= 0x2C00 && c <= 0x2FEF
            || c >= 0x3001 && c <= 0xD7FF
            || c >= 0xF900 && c <= 0xFDCF
            || c >= 0xFDF0 && c <= 0xFFFD
            || c >= 0x10000 && c <= 0xEFFFF;
    }

    // The NameChar production of the XML specification.
    private static boolean isNameChar(final int c) {
        return isNameStartChar(c)
            || c == '-' || c == '.'
            || c >= '0' && c <= '9'
            || c == 0xB7
            || c >= 0x300 && c <= 0x36F
            || c >= 0x203F && c <= 0x2040;
    }

    /**
     * Reads the value of a field, falling back to the value of its @XmlDefaultValue annotation
     * when the field itself holds null. A null field carrying no such annotation fails here with
     * a NullPointerException.
     * @param f the field to read
     * @param o the object the field belongs to
     * @return the field's value, or its declared default
     * @throws IllegalAccessException if the field cannot be read
     */
    public static Object toValue(final Field f, final Object o) throws IllegalAccessException {
        final var value = f.get(o);
        if (value != null) return value;
        return f.getAnnotation(XmlDefaultValue.class).value();
    }

    /**
     * Writes an element with its text inside it, or a self-closing tag when there is no text.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @param text the text of the element, or null or empty for a self-closing tag
     * @throws IOException if the writer fails
     */
    public static void writeTag(final Writer writer, final String name, final String text) throws IOException {
        if (isNullOrEmpty(text)) {
            writeSelfClosingTag(writer, name);
        } else {
            writeOpeningAndClosingTag(writer, name, text);
        }
    }
    /**
     * Writes an element with its attributes and its text inside it, or a self-closing tag when
     * there is no text. The attributes are written exactly as given, after the name.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @param attributes the rendered attributes, written as they stand
     * @param text the text of the element, or null or empty for a self-closing tag
     * @throws IOException if the writer fails
     */
    public static void writeTag(final Writer writer, final String name, final String attributes, final String text) throws IOException {
        if (isNullOrEmpty(text)) {
            writeSelfClosingTag(writer, name, attributes);
        } else {
            writeOpeningTag(writer, name, attributes);
            writer.append(text);
            writeClosingTag(writer, name);
        }
    }
    /**
     * Writes an opening tag, its text and the closing tag as three pieces, making no decision of
     * its own about self-closing tags.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @param text the text to put between the tags
     * @throws IOException if the writer fails
     */
    public static void writeOpeningAndClosingTag(final Writer writer, final String name, final String text) throws IOException {
        writeOpeningTag(writer, name);
        // Writer.append(null) writes the four characters "null", so a null text writes nothing
        if (text != null) writer.append(text);
        writeClosingTag(writer, name);
    }
    /**
     * Writes an opening tag with nothing between the name and the closing bracket.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @throws IOException if the writer fails
     */
    public static void writeOpeningTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(name).append(GREATER_THAN);
    }
    /**
     * Writes an opening tag with the rendered attributes between the name and the closing
     * bracket.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @param attributes the rendered attributes, written as they stand
     * @throws IOException if the writer fails
     */
    public static void writeOpeningTag(final Writer writer, final String name, final String attributes) throws IOException {
        writer.append(LESS_THAN).append(name).append(attributes).append(GREATER_THAN);
    }
    /**
     * Writes a closing tag, which carries no attributes.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @throws IOException if the writer fails
     */
    public static void writeClosingTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(FORWARD_SLASH).append(name).append(GREATER_THAN);
    }
    /**
     * Writes an element that has neither attributes nor text in it.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @throws IOException if the writer fails
     */
    public static void writeSelfClosingTag(final Writer writer, final String name) throws IOException {
        writer.append(LESS_THAN).append(name).append(FORWARD_SLASH).append(GREATER_THAN);
    }
    /**
     * Writes an element that has attributes but no text in it.
     * @param writer the writer to write to
     * @param name the name of the tag
     * @param attributes the rendered attributes, written as they stand
     * @throws IOException if the writer fails
     */
    public static void writeSelfClosingTag(final Writer writer, final String name, final String attributes) throws IOException {
        writer.append(LESS_THAN).append(name).append(attributes).append(SPACE).append(FORWARD_SLASH).append(GREATER_THAN);
    }

}
