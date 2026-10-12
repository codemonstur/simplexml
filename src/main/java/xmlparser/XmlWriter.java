package xmlparser;

import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlMapWithChildNodes;
import xmlparser.error.InvalidAnnotation;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;
import xmlparser.parsing.ObjectSerializer;
import xmlparser.utils.AccessSerializers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static xmlparser.utils.Constants.EMPTY;
import static xmlparser.utils.Constants.INDENT;
import static xmlparser.utils.Reflection.*;
import static xmlparser.utils.Validator.multipleAreTrue;
import static xmlparser.utils.XML.*;

/**
 * The writing half of the mapper: turns an object or a DOM tree into XML by reflecting over it
 * and asking the serializer registered for each type how it wants to be written. It is an
 * interface so that XmlParser can configure the escaping, the pretty printing and the line
 * ending; writing a document usually goes through XmlParser rather than through here.
 */
public interface XmlWriter extends AccessSerializers {

    /**
     * The line ending an implementation wants after every tag when the output is pretty printed.
     *
     * @return the line ending to write, for example a line feed
     */
    String newLine();

    /**
     * Escapes a text or an attribute value an implementation's way, so it can be written between
     * tags or inside a quoted attribute without being read back as markup.
     *
     * @param input the raw text to escape
     * @return the escaped form, safe to write into the document
     */
    String escape(String input);

    /**
     * Whether an implementation wants the output indented and broken over lines; without it tags
     * are written one after another with nothing between them.
     *
     * @return true to pretty print, false to write flat
     */
    boolean shouldPrettyPrint();

    /**
     * Writes an object as a single element, named after its class, and returns it as a string. A
     * DOM element goes out as the tree it is rather than being reflected over, whose parent pointer
     * would walk the cycle forever; a field that cannot be read or a failure of the string itself
     * comes back as {@link InvalidXml} rather than a checked exception.
     *
     * @param o the object to write
     * @return the XML written for it
     */
    default String toXml(final Object o) {
        // A DOM tree points at its parent, so reflecting over one would walk the cycle forever
        if (o instanceof XmlElement) return domToXml((XmlElement) o);

        final StringWriter output = new StringWriter();

        // A failure part way through would otherwise hand back XML that is missing everything
        // after it, so the two ways this can go wrong are turned into an error instead
        try { writeObject(output, toName(o.getClass()), o, EMPTY); }
        catch (final IllegalArgumentException | IllegalAccessException e) {
            throw new InvalidXml("Could not read the fields of " + o.getClass().getName(), e);
        }
        catch (final IOException e) {
            throw new InvalidXml("Failed to write the XML for " + o.getClass().getName(), e);
        }

        return output.toString();
    }
    /**
     * Writes an object as a single element, named after its class, to the writer. A DOM element
     * goes out as the tree it is rather than being reflected over, and a field that cannot be read
     * comes back as {@link InvalidXml} rather than a checked exception.
     *
     * @param o the object to write
     * @param writer the writer to write it to
     * @throws IOException if the writer fails
     */
    default void toXml(final Object o, final Writer writer) throws IOException {
        if (o instanceof XmlElement) { domToXml((XmlElement) o, writer); return; }

        try { writeObject(writer, toName(o.getClass()), o, EMPTY); }
        catch (final IllegalArgumentException | IllegalAccessException e) {
            throw new InvalidXml("Could not read the fields of " + o.getClass().getName(), e);
        }
    }

    /**
     * Writes a DOM tree to a string, starting at no indentation. A failure to write to the string
     * comes back as {@link InvalidXml} rather than a checked exception.
     *
     * @param node the element to write, the root of the tree
     * @return the XML written for it
     */
    default String domToXml(final XmlElement node) {
        final StringWriter output = new StringWriter();

        try { domToXml(node, output); }
        catch (final IOException e) {
            throw new InvalidXml("Failed to write the XML for <" + node.name + ">", e);
        }

        return output.toString();
    }
    /**
     * Writes a DOM tree to the writer, starting at no indentation.
     *
     * @param node the element to write, the root of the tree
     * @param writer the writer to write it to
     * @throws IOException if the writer fails
     */
    default void domToXml(final XmlElement node, final Writer writer) throws IOException {
        domToXml(node, writer, "");
    }

    /**
     * A text element holds no name, no attributes and no children of its own: it is the text
     * itself, so it is written as that text and carries no tags. What comes out is a fragment
     * rather than a document, because there is nothing to wrap it in, so it cannot be read back.
     *
     * @param node the element to write
     * @param writer the writer to write it to
     * @param indent the indentation to write it at, an empty string for none
     * @throws IOException if the writer fails
     */
    default void domToXml(final XmlElement node, final Writer writer, final String indent) throws IOException {
        if (node instanceof XmlTextElement) {
            final String text = ((XmlTextElement) node).text;
            if (text != null && !text.isEmpty()) writer.append(escape(text));
            return;
        }

        final String text = node.getText();
        if (text == null && node.children.isEmpty()) {
            writeIndent(writer, indent);
            writeSelfClosingTag(writer, node.name, attributesToXml(node.attributes, this::escape));
            writeNewLine(writer);
        } else if (!node.hasNonTextChildren() && node.attributes.isEmpty()) {
            writeIndent(writer, indent);
            // an element whose text children hold nothing has nothing to write between the tags
            writeOpeningAndClosingTag(writer, node.name, text == null ? null : escape(text));
            writeNewLine(writer);
        } else {
            writeIndent(writer, indent);
            writeOpeningTag(writer, node.name, attributesToXml(node.attributes, this::escape));
            writeNewLine(writer);
            // Children are written in the order they were read, and neighbouring text nodes are
            // joined so that reading the output back produces the same text in the same place
            final StringBuilder run = new StringBuilder();
            for (final XmlElement child : node.children) {
                if (child instanceof XmlTextElement) {
                    run.append(((XmlTextElement) child).text);
                    continue;
                }
                writeTextRun(writer, indent, run.toString());
                run.setLength(0);
                domToXml(child, writer, INDENT+indent);
            }
            writeTextRun(writer, indent, run.toString());
            writeIndent(writer, indent);
            writeClosingTag(writer, node.name);
            writeNewLine(writer);
        }
    }

    private void writeTextRun(final Writer writer, final String indent, final String text) throws IOException {
        if (text.isEmpty()) return;

        writeIndent(writer, INDENT+indent);
        writer.append(escape(text));
        writeNewLine(writer);
    }

    private void writeSimple(final Writer writer, final String name, final Object value, final String indent) throws IOException {
        writeIndent(writer, indent);
        writeTag(writer, name, escape(getSerializer(value.getClass()).convert(value)));
        writeNewLine(writer);
    }

    private void writeSimple(final Writer writer, final String name, final Object value, final List<Field> attributes
            , final Object text, final String indent) throws IOException, IllegalAccessException {
        writeIndent(writer, indent);
        writeTag(writer, name, attributesToXml(attributes, value, this::escape, this::render),
                escape(getSerializer(text.getClass()).convert(text)));
        writeNewLine(writer);
    }

    private void writeList(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (Iterable<?>) o) {
            writeItem(field, writer, name, item, indent);
        }
    }

    private void writeArray(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (int i = 0; i < Array.getLength(o); i++) {
            writeItem(field, writer, name, Array.get(o, i), indent);
        }
    }

    private void writeSet(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (Set<?>) o) {
            writeItem(field, writer, name, item, indent);
        }
    }

    /**
     * Writes a single collection element, or a single map entry value. A null item is written as
     * an empty tag, which reads back as a null value.
     */
    private void writeItem(final Field field, final Writer writer, final String name, final Object item, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        writeField(item == null ? Object.class : item.getClass(), field, writer, name, item, indent);
    }

    /**
     * Serializes a map value, returning null when there is no value, so the caller can write an
     * empty tag.
     */
    private String mapValueToXml(final ObjectSerializer serializer, final Object value) {
        return value == null ? null : escape(toMapText(serializer, value));
    }

    /**
     * Serializes a map key that is written as text: an attribute value or a tag name.
     */
    private String mapKeyToXml(final ObjectSerializer serializer, final Object key) {
        return escape(toMapText(serializer, key));
    }

    /**
     * Converts a map key or value that is written as text. Enums are written as their XmlEnumValue,
     * the same way enum fields are, and everything else uses its serializer.
     */
    private String toMapText(final ObjectSerializer serializer, final Object value) {
        return value instanceof Enum ? toEnumValue((Enum) value) : serializer.convert(value);
    }

    /**
     * Turns a value into the text it is written as: an enum by its @XmlEnumValue or its name, and
     * everything else through the serializer registered for its type, which falls back to the
     * value's own toString. Used for attributes and for text nodes, so that they are written the
     * same way an element's text is.
     * @param value the value to render, which is not null
     * @return the text the value is written as
     */
    private String render(final Object value) {
        return toMapText(getSerializer(value.getClass()), value);
    }

    private void writeMap(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {

        final boolean isXmlMapTagIsKey = field.isAnnotationPresent(XmlMapTagIsKey.class);
        final boolean isXmlMapWithAttributes = field.isAnnotationPresent(XmlMapWithAttributes.class);
        final boolean isXmlMapWithChildNodes = field.isAnnotationPresent(XmlMapWithChildNodes.class);

        if (multipleAreTrue(isXmlMapTagIsKey, isXmlMapWithAttributes, isXmlMapWithChildNodes))
            throw new InvalidAnnotation("Only one of XmlMapTagIsKey, XmlMapWithAttributes and XmlMapWithChildNodes is allowed per field");

        final ParameterizedType type = (ParameterizedType) field.getGenericType();
        final Map<?,?> map = (Map<?,?>) o;
        final ObjectSerializer convKey = getSerializer(toClassOfMapKey(type));
        final ObjectSerializer convVal = getSerializer(toClassOfMapValue(type));

        if (isXmlMapWithAttributes) {
            final XmlMapWithAttributes annotation = field.getAnnotation(XmlMapWithAttributes.class);
            final String keyName = annotation.keyName();
            final String valueName = annotation.valueName();

            for (final Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() == null) continue;
                writeIndent(writer, indent);
                if (valueName.isEmpty()) {
                    final StringBuilder builder = new StringBuilder();
                    addAttribute(builder, keyName, mapKeyToXml(convKey, entry.getKey()));
                    writeTag(writer, name, builder.toString(), mapValueToXml(convVal, entry.getValue()));
                } else {
                    final StringBuilder builder = new StringBuilder();
                    addAttribute(builder, keyName, mapKeyToXml(convKey, entry.getKey()));
                    final String value = mapValueToXml(convVal, entry.getValue());
                    // an absent attribute reads back as a null value, an empty one as an empty value
                    if (value != null) addAttribute(builder, valueName, value);
                    writeSelfClosingTag(writer, name, builder.toString());
                }
                writeNewLine(writer);
            }
            return;
        }

        if (isXmlMapWithChildNodes) {
            final XmlMapWithChildNodes annotation = field.getAnnotation(XmlMapWithChildNodes.class);
            final String keyName = annotation.keyName();
            final String valueName = annotation.valueName();

            for (final Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() == null) continue;
                writeIndent(writer, indent);
                writeOpeningTag(writer, name);
                writeNewLine(writer);
                writeItem(field, writer, keyName, entry.getKey(), indent+INDENT);
                if (valueName.isEmpty()) {
                    final String value = mapValueToXml(convVal, entry.getValue());
                    if (value != null) {
                        writeIndent(writer, indent+INDENT);
                        writer.append(value);
                        writeNewLine(writer);
                    }
                } else {
                    writeItem(field, writer, valueName, entry.getValue(), indent+INDENT);
                }
                writeIndent(writer, indent);
                writeClosingTag(writer, name);
                writeNewLine(writer);
            }
            return;
        }

        writeIndent(writer, indent);
        writeOpeningTag(writer, name);
        writeNewLine(writer);
        for (final Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() == null) continue;
            final String key = toMapText(convKey, entry.getKey());
            // the key is a tag name, so a value that can not be one can not be written either
            if (!isValidTagName(key))
                throw new InvalidXml("Map key '" + key + "' is not a valid tag name");
            writeItem(field, writer, key, entry.getValue(), indent+INDENT);
        }
        writeIndent(writer, indent);
        writeClosingTag(writer, name);
        writeNewLine(writer);
    }

    private void writeEnum(final Writer writer, final String name, final Enum o, final String indent) throws IOException {
        writeIndent(writer, indent);
        writeTag(writer, name, escape(toEnumValue(o)));
        writeNewLine(writer);
    }

    private void writeObject(final Writer writer, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        final List<Field> attributes = new LinkedList<>();
        final List<Field> childNodes = new LinkedList<>();
        final Field textNode = determineTypeOfFields(o.getClass(), o, attributes, childNodes);

        if (childNodes.isEmpty()) {
            if (textNode == null) {
                writeIndent(writer, indent);
                writeSelfClosingTag(writer, name, attributesToXml(attributes, o, this::escape, this::render));
                writeNewLine(writer);
            }
            else
                writeSimple(writer, name, o, attributes, toValue(textNode, o), indent);
            return;
        }

        writeIndent(writer, indent);
        writeOpeningTag(writer, name, attributesToXml(attributes, o, this::escape, this::render));
        writeNewLine(writer);

        for (final Field f : childNodes) {
            final Object raw = f.get(o);
            // a null field carries a @XmlDefaultValue, which is always a String, so dispatch on it
            final Object value = raw != null ? raw : toValue(f, o);
            // a field declared as Object says nothing about the value's own shape, so the value's
            // class decides how it is written rather than the declared type
            final Class<?> type = raw == null || f.getType().equals(Object.class) ? value.getClass() : f.getType();
            final String fieldName = toName(f, raw);
            if (isWrapped(f)) {
                writeIndent(writer, indent+INDENT);
                writeOpeningTag(writer, toWrappedName(f));
                writeNewLine(writer);
                writeField(type, f, writer, fieldName, value, indent+INDENT+INDENT);
                writeIndent(writer, indent+INDENT);
                writeClosingTag(writer, toWrappedName(f));
                writeNewLine(writer);
            } else {
                writeField(type, f, writer, fieldName, value, indent+INDENT);
            }
        }
        if (textNode != null) {
            writeIndent(writer, indent);
            writer.append(escape(render(toValue(textNode, o))));
            writeNewLine(writer);
        }
        writeIndent(writer, indent);
        writeClosingTag(writer, name);
        writeNewLine(writer);
    }

    private void writeField(final Class<?> c, final Field field, final Writer writer,
            final String name, final Object value, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        if (value == null) {
            writeIndent(writer, indent);
            writeTag(writer, name, (String) null);
            writeNewLine(writer);
            return;
        }
        switch (toClassType(c, this)) {
            case SIMPLE -> writeSimple(writer, name, value, indent);
            case ARRAY -> writeArray(writer, field, name, value, indent);
            case LIST -> writeList(writer, field, name, value, indent);
            case SET -> writeSet(writer, field, name, value, indent);
            case MAP -> writeMap(writer, field, name, value, indent);
            case ENUM -> writeEnum(writer, name, (Enum)value, indent);
            default -> writeObject(writer, name, value, indent);
        }
    }

    private void writeIndent(final Writer writer, final String indent) throws IOException {
        if (shouldPrettyPrint()) writer.append(indent);
    }
    private void writeNewLine(final Writer writer) throws IOException {
        if (shouldPrettyPrint()) writer.append(newLine());
    }

}
