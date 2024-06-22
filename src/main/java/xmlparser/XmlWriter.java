package xmlparser;

import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlMapWithChildNodes;
import xmlparser.error.InvalidAnnotation;
import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;
import xmlparser.parsing.ObjectSerializer;
import xmlparser.utils.AccessSerializers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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

public interface XmlWriter extends AccessSerializers {

    String newLine();
    String escape(String input);
    boolean shouldPrettyPrint();

    default String toXml(final Object o) {
        final StringWriter output = new StringWriter();

        try { writeObject(output, toName(o.getClass()), o, EMPTY); }
        catch (final IllegalArgumentException | IllegalAccessException | IOException ignored) { /* can't happen */ }

        return output.toString();
    }
    default void toXml(final Object o, final Writer writer) throws IOException {
        try { writeObject(writer, toName(o.getClass()), o, EMPTY); }
        catch (final IllegalArgumentException | IllegalAccessException ignored) { /* can't happen */ }
    }

    default String domToXml(final XmlElement node) {
        final StringWriter output = new StringWriter();

        try { domToXml(node, output); }
        catch (final IOException ignored) { /* can't happen */ }

        return output.toString();
    }
    default void domToXml(final XmlElement node, final Writer writer) throws IOException {
        domToXml(node, writer, "");
    }
    default void domToXml(final XmlElement node, final Writer writer, final String indent) throws IOException {
        final String text = node.getText();
        if (text == null && node.children.isEmpty()) {
            writeIndent(writer, indent);
            writeSelfClosingTag(writer, node.name, attributesToXml(node.attributes, this::escape));
            writeNewLine(writer);
        } else if (!node.hasNonTextChildren() && node.attributes.isEmpty()) {
            writeIndent(writer, indent);
            writeOpeningAndClosingTag(writer, node.name, escape(text));
            writeNewLine(writer);
        } else {
            writeIndent(writer, indent);
            writeOpeningTag(writer, node.name, attributesToXml(node.attributes, this::escape));
            writeNewLine(writer);
            for (final XmlElement child : node.children) {
                if (child instanceof XmlTextElement) continue;

                domToXml(child, writer, INDENT+indent);
            }
            if (text != null) {
                writeIndent(writer, INDENT+indent);
                writer.append(escape(text));
                writeNewLine(writer);
            }
            writeIndent(writer, indent);
            writeClosingTag(writer, node.name);
            writeNewLine(writer);
        }
    }

    private void writeSimple(final Writer writer, final String name, final Object value, final String indent) throws IOException {
        writeIndent(writer, indent);
        writeTag(writer, name, escape(getSerializer(value.getClass()).convert(value)));
        writeNewLine(writer);
    }

    private void writeSimple(final Writer writer, final String name, final Object value, final List<Field> attributes
            , final Object text, final String indent) throws IOException, IllegalAccessException {
        writeIndent(writer, indent);
        writeTag(writer, name, attributesToXml(attributes, value, this::escape),
                escape(getSerializer(text.getClass()).convert(text)));
        writeNewLine(writer);
    }

    private void writeList(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (List<?>) o) {
            writeField(item.getClass(), field, writer, name, item, indent);
        }
    }

    private void writeArray(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (Object[]) o) {
            writeField(item.getClass(), field, writer, name, item, indent);
        }
    }

    private void writeSet(final Writer writer, final Field field, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (Set<?>) o) {
            writeField(item.getClass(), field, writer, name, item, indent);
        }
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
                writeIndent(writer, indent);
                if (valueName.isEmpty()) {
                    final StringBuilder builder = new StringBuilder();
                    addAttribute(builder, keyName, escape(convKey.convert(entry.getKey())));
                    writeTag(writer, name, builder.toString(), escape(convVal.convert(entry.getValue())));
                } else {
                    final StringBuilder builder = new StringBuilder();
                    addAttribute(builder, keyName, escape(convKey.convert(entry.getKey())));
                    addAttribute(builder, valueName, escape(convVal.convert(entry.getValue())));
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
                writeIndent(writer, indent);
                writeOpeningTag(writer, name);
                writeNewLine(writer);
                writeField(entry.getKey().getClass(), field, writer, keyName, entry.getKey(), indent+INDENT);
                if (valueName.isEmpty()) {
                    writeIndent(writer, indent+INDENT);
                    writer.append(escape(convVal.convert(entry.getValue())));
                    writeNewLine(writer);
                } else {
                    writeField(entry.getValue().getClass(), field, writer, valueName, entry.getValue(), indent+INDENT);
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
            writeField(entry.getValue().getClass(), field, writer, entry.getKey().toString(), entry.getValue(), indent+INDENT);
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
                writeSelfClosingTag(writer, name, attributesToXml(attributes, o, this::escape));
                writeNewLine(writer);
            }
            else
                writeSimple(writer, name, o, attributes, textNode.get(o), indent);
            return;
        }

        writeIndent(writer, indent);
        writeOpeningTag(writer, name, attributesToXml(attributes, o, this::escape));
        writeNewLine(writer);

        for (final Field f : childNodes) {
            if (isWrapped(f)) {
                writeIndent(writer, indent+INDENT);
                writeOpeningTag(writer, toWrappedName(f));
                writeNewLine(writer);
                writeField(f.getType(), f, writer, toName(f, f.get(o)), f.get(o), indent+INDENT+INDENT);
                writeIndent(writer, indent+INDENT);
                writeClosingTag(writer, toWrappedName(f));
                writeNewLine(writer);
            } else {
                writeField(f.getType(), f, writer, toName(f, f.get(o)), f.get(o), indent+INDENT);
            }
        }
        if (textNode != null) {
            writeIndent(writer, indent);
            writer.append(escape(textNode.get(o).toString()));
            writeNewLine(writer);
        }
        writeIndent(writer, indent);
        writeClosingTag(writer, name);
        writeNewLine(writer);
    }

    private void writeField(final Class<?> c, final Field field, final Writer writer,
            final String name, final Object value, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
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
