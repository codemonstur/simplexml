package simplexml;

import simplexml.model.ElementNode;
import simplexml.model.XmlAttribute;
import simplexml.model.XmlNoExport;
import simplexml.model.XmlTextNode;
import simplexml.utils.Accessors.AccessSerializers;
import simplexml.utils.Accessors.ParserConfiguration;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static simplexml.utils.Constants.*;
import static simplexml.utils.Functions.*;

public interface XmlWriter extends AccessSerializers, ParserConfiguration {

    default String toXml(final Object o) {
        return toXml(o, toName(o.getClass()));
    }

    default String toXml(final Object o, final String name) {
        final StringWriter output = new StringWriter();

        try {
            writeObject(output, name, o, EMPTY);
        } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
            // can't happen
        }

        return output.toString();
    }

    default void toXml(final Object o, final Writer writer) throws IOException {
        toXml(o, toName(o.getClass()), writer);
    }
    default void toXml(final Object o, final String name, final Writer writer) throws IOException {
        try {
            writeObject(writer, name, o, EMPTY);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // can't happen
        }
    }

    default void writeSimple(final Writer writer, final String name, final Object value, final String indent) throws IOException {
        final String output = getSerializer(value.getClass()).convert(value);
        writer.append(indent);
        if (output.isEmpty()) {
            writer.append(LESS_THAN).append(name).append(FORWARD_SLASH).append(GREATER_THAN).append(NEW_LINE);
            return;
        }
        writer.append(LESS_THAN).append(name).append(GREATER_THAN)
              .append(escapeXml(output, shouldEncodeUTF8()))
              .append(LESS_THAN).append(FORWARD_SLASH).append(name).append(GREATER_THAN)
              .append(NEW_LINE);
    }

    default void writeList(final Writer writer, final String name,
            final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        final List<?> list = (List<?>) o;
        for (final Object item : list) {
            writeField(item.getClass(), writer, name, item, indent);
        }
    }

    default void writeArray(final Writer writer, final String name,
            final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        final Object[] list = (Object[]) o;
        for (final Object item : list) {
            writeField(item.getClass(), writer, name, item, indent);
        }
    }

    default void writeSet(final Writer writer, final String name,
            final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        final Set<?> set = (Set<?>) o;
        for (final Object item : set) {
            writeField(item.getClass(), writer, name, item, indent);
        }
    }

    default void writeMap(final Writer writer, final Object o,
            final String indent) throws IllegalArgumentException,
            IllegalAccessException, IOException {
        final Map<?, ?> map = (Map<?,?>) o;
        for (final Entry<?, ?> entry : map.entrySet()) {
            writeField(entry.getValue().getClass(), writer, entry.getKey().toString(), entry.getValue(), indent);
        }
    }

    default void writeObject(final Writer writer, final String name,
            final Object o, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        final Class<?> clazz = o.getClass();

        Field textNode = null;
        final List<Field> attributes = new LinkedList<>();
        final List<Field> childnodes = new LinkedList<>();
        for (final Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(o) == null) continue;
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (f.isAnnotationPresent(XmlNoExport.class)) continue;
            if (f.isAnnotationPresent(XmlTextNode.class))
                textNode = f;
            else if (f.isAnnotationPresent(XmlAttribute.class))
                attributes.add(f);
            else
                childnodes.add(f);
        }

        if (childnodes.isEmpty()) {
            writer.append(indent);
            writer.append(LESS_THAN);
            writer.append(name);
            writer.append(buildAttributes(attributes, o, shouldEncodeUTF8()));
            if (textNode == null) {
                writer.append(SPACE);
                writer.append(FORWARD_SLASH);
            }
            else {
                writer.append(GREATER_THAN);
                writer.append(escapeXml(textNode.get(o).toString(), shouldEncodeUTF8()));
                writer.append(LESS_THAN);
                writer.append(FORWARD_SLASH);
                writer.append(name);
            }
            writer.append(GREATER_THAN);
            writer.append(NEW_LINE);
            return;
        }
        
        writer.append(indent);
        writer.append(LESS_THAN);
        writer.append(name);
        writer.append(buildAttributes(attributes, o, shouldEncodeUTF8()));
        writer.append(GREATER_THAN);
        writer.append(NEW_LINE);

        for (final Field f : childnodes) {
            writeField(f.getType(), writer, toName(f), f.get(o), indent+INDENT);
        }
        if (textNode != null) {
            writer.append(indent);
            writer.append(escapeXml(textNode.get(o).toString(), shouldEncodeUTF8()));
            writer.append(NEW_LINE);
        }
        writer.append(indent);
        writer.append(LESS_THAN);
        writer.append(FORWARD_SLASH);
        writer.append(name);
        writer.append(GREATER_THAN);
        writer.append(NEW_LINE);
    }

    static String buildAttributes(final List<Field> fields, final Object o, final boolean shouldEncodeUTF8) throws IllegalArgumentException,
            IllegalAccessException {
        final StringBuilder attr = new StringBuilder(6*fields.size());
        for (final Field f : fields) {
            attr.append(SPACE)
                .append(toName(f))
                .append(EQUALS)
                .append(DOUBLE_QUOTE)
                .append(escapeXml(f.get(o).toString(), shouldEncodeUTF8))
                .append(DOUBLE_QUOTE);
        }
        return attr.toString();
    }

    default void writeField(final Class<?> c, final Writer writer,
            final String name, final Object value, final String indent)
            throws IllegalArgumentException, IllegalAccessException, IOException {
        if (isSimple(c) || hasSerializer(c))
            writeSimple(writer, name, value, indent);
        else
        if (c.isArray())
            writeArray(writer, name, value, indent);
        else
        if (isList(c))
            writeList(writer, name, value, indent);
        else
        if (isSet(c))
            writeSet(writer, name, value, indent);
        else
        if (isMap(c))
            writeMap(writer, value, indent);
        else
            writeObject(writer, name, value, indent);
    }

    default String domToXml(final ElementNode node) {
        final StringWriter output = new StringWriter();
        try {
            domToXml(node, output);
        } catch (IOException e) {
            // can't happen
        }
        return output.toString();
    }
    default void domToXml(final ElementNode node, final Writer writer) throws IOException {
        if (node.text == null && node.children.isEmpty()) {
            writer.append(LESS_THAN).append(node.name).append(attributesToXml(node.attributes, shouldEncodeUTF8()))
                  .append(SPACE).append(FORWARD_SLASH).append(GREATER_THAN);
        } else {
            writer.append(LESS_THAN).append(node.name).append(attributesToXml(node.attributes, shouldEncodeUTF8())).append(GREATER_THAN);
            for (final ElementNode child : node.children) {
                domToXml(child, writer);
            }
            if (node.text != null) writer.append(escapeXml(node.text, shouldEncodeUTF8()));
            writer.append(LESS_THAN).append(FORWARD_SLASH).append(node.name).append(GREATER_THAN);
        }
    }
    static String attributesToXml(final Map<String, String> map, final boolean escapeUTF8) {
        if (map == null || map.isEmpty()) return EMPTY;

        final StringBuilder builder = new StringBuilder();
        for (final Entry<String, String> entry : map.entrySet()) {
            builder.append(SPACE).append(entry.getKey()).append(EQUALS).append(DOUBLE_QUOTE)
                   .append(escapeXml(entry.getValue(), escapeUTF8)).append(DOUBLE_QUOTE);
        }
        return builder.toString();
    }
}
