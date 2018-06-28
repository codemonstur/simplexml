package simplexml;

import simplexml.model.Element;
import simplexml.utils.Interfaces.AccessSerializers;
import simplexml.utils.Interfaces.ParserConfiguration;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static simplexml.utils.Constants.*;
import static simplexml.utils.Reflection.*;
import static simplexml.utils.XML.*;

public interface XmlWriter extends AccessSerializers, ParserConfiguration {

    default String toXml(final Object o, final String name) {
        final StringWriter output = new StringWriter();

        try { writeObject(output, name, o, EMPTY); }
        catch (IllegalArgumentException | IllegalAccessException | IOException e) { /* can't happen */ }

        return output.toString();
    }
    default void toXml(final Object o, final String name, final Writer writer) throws IOException {
        try { writeObject(writer, name, o, EMPTY); }
        catch (IllegalArgumentException | IllegalAccessException e) { /* can't happen */ }
    }

    default String domToXml(final Element node) {
        final StringWriter output = new StringWriter();

        try { domToXml(node, output); }
        catch (IOException e) { /* can't happen */ }

        return output.toString();
    }
    default void domToXml(final Element node, final Writer writer) throws IOException {
        domToXml(node, writer, "");
    }
    default void domToXml(final Element node, final Writer writer, final String indent) throws IOException {
        if (node.text == null && node.children.isEmpty()) {
            writeIndent(writer, indent);
            writeSelfClosingTag(writer, node.name, attributesToXml(node.attributes, shouldEncodeUTF8()));
            writeNewLine(writer);
        } else if (node.children.isEmpty() && node.attributes.isEmpty()) {
            writeIndent(writer, indent);
            writeOpeningAndClosingTag(writer, node.name, node.text);
            writeNewLine(writer);
        } else {
            writeIndent(writer, indent);
            writeOpeningTag(writer, node.name, attributesToXml(node.attributes, shouldEncodeUTF8()));
            writeNewLine(writer);
            for (final Element child : node.children) {
                domToXml(child, writer, INDENT+indent);
            }
            if (node.text != null) {
                writeIndent(writer, indent);
                writer.append(escapeXml(node.text, shouldEncodeUTF8()));
            }
            writeIndent(writer, indent);
            writeClosingTag(writer, node.name);
            writeNewLine(writer);
        }
    }


    default void writeSimple(final Writer writer, final String name, final Object value, final String indent) throws IOException {
        writeIndent(writer, indent);
        writeTag(writer, name, escapeXml(getSerializer(value.getClass()).convert(value), shouldEncodeUTF8()));
        writeNewLine(writer);
    }

    default void writeSimple(final Writer writer, final String name, final Object value, final List<Field> attributes
            , final Object text, final String indent) throws IOException, IllegalAccessException {
        writeIndent(writer, indent);
        writeTag(writer, name, attributesToXml(attributes, value, shouldEncodeUTF8()),
                escapeXml(getSerializer(text.getClass()).convert(text), shouldEncodeUTF8()));
        writeNewLine(writer);
    }

    default void writeList(final Writer writer, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (List<?>) o) {
            writeField(item.getClass(), writer, name, item, indent);
        }
    }

    default void writeArray(final Writer writer, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (Object[]) o) {
            writeField(item.getClass(), writer, name, item, indent);
        }
    }

    default void writeSet(final Writer writer, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        for (final Object item : (Set<?>) o) {
            writeField(item.getClass(), writer, name, item, indent);
        }
    }

    default void writeMap(final Writer writer, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        writeIndent(writer, indent);
        writeOpeningTag(writer, name);
        writeNewLine(writer);
        for (final Entry<?, ?> entry : ((Map<?,?>) o).entrySet()) {
            writeField(entry.getValue().getClass(), writer, entry.getKey().toString(), entry.getValue(), indent+INDENT);
        }
        writeIndent(writer, indent);
        writeClosingTag(writer, name);
        writeNewLine(writer);
    }

    default void writeObject(final Writer writer, final String name, final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        final List<Field> attributes = new LinkedList<>();
        final List<Field> childNodes = new LinkedList<>();
        final Field textNode = determineTypeOfFields(o.getClass(), o, attributes, childNodes);

        if (childNodes.isEmpty()) {
            writeSimple(writer, name, o, attributes, textNode.get(o), indent);
            return;
        }

        writeIndent(writer, indent);
        writeOpeningTag(writer, name, attributesToXml(attributes, o, shouldEncodeUTF8()));
        writeNewLine(writer);

        for (final Field f : childNodes) {
            boolean isWrapped = isWrapped(f);
            if (isWrapped) {
                writeIndent(writer, indent+INDENT);
                writeOpeningTag(writer, toWrappedName(f));
                writeNewLine(writer);
                writeField(f.getType(), writer, toName(f), f.get(o), indent+INDENT+INDENT);
                writeIndent(writer, indent+INDENT);
                writeClosingTag(writer, toWrappedName(f));
                writeNewLine(writer);
            } else {
                writeField(f.getType(), writer, toName(f), f.get(o), indent+INDENT);
            }
        }
        if (textNode != null) {
            writeIndent(writer, indent);
            writer.append(escapeXml(textNode.get(o).toString(), shouldEncodeUTF8()));
            writeNewLine(writer);
        }
        writeIndent(writer, indent);
        writeClosingTag(writer, name);
        writeNewLine(writer);
    }

    default void writeField(final Class<?> c, final Writer writer,
            final String name, final Object value, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        switch (toClassType(c, this)) {
            case SIMPLE: writeSimple(writer, name, value, indent); break;
            case ARRAY: writeArray(writer, name, value, indent); break;
            case LIST: writeList(writer, name, value, indent); break;
            case SET: writeSet(writer, name, value, indent); break;
            case MAP: writeMap(writer, name, value, indent); break;
            default: writeObject(writer, name, value, indent); break;
        }
    }

    default void writeIndent(final Writer writer, final String indent) throws IOException {
        if (shouldPrettyPrint()) writer.append(indent);
    }
    default void writeNewLine(final Writer writer) throws IOException {
        if (shouldPrettyPrint()) writer.append(NEW_LINE);
    }

}
