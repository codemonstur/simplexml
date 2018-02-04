package simplexml;

import simplexml.model.*;

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

import static simplexml.core.Constants.*;
import static simplexml.core.Utils.*;

public interface XmlWriter extends AccessSerializers, ParserConfiguration {

    default String toXml(final Object o) {
        return toXml(o, o.getClass().getSimpleName().toLowerCase());
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
        toXml(o, o.getClass().getSimpleName().toLowerCase(), writer);
    }
    default void toXml(final Object o, final String name, final Writer writer) throws IOException {
        try {
            writeObject(writer, name, o, EMPTY);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // can't happen
        }
    }

    default void writeSimple(final Writer writer, final String name, final Object value, final String indent) throws IOException {
        writer.append(indent)
              .append(LESS_THAN).append(name).append(GREATER_THAN)
              .append(escapeXml(getSerializer(value.getClass()).convert(value), shouldEncodeUTF8()))
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

        String fieldName;
        for (final Field f : childnodes) {
            fieldName = (f.isAnnotationPresent(XmlName.class)) ? f.getAnnotation(XmlName.class).value() : f.getName();
            writeField(f.getType(), writer, fieldName, f.get(o), indent+INDENT);
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
        final StringBuilder attr = new StringBuilder();
        for (final Field f : fields) {
            attr.append(SPACE);
            if (f.isAnnotationPresent(XmlName.class))
                attr.append(f.getAnnotation(XmlName.class).value());
            else
                attr.append(f.getName());
            attr.append(EQUALS);
            attr.append(DOUBLE_QUOTE);
            attr.append(escapeXml(f.get(o).toString(), shouldEncodeUTF8));
            attr.append(DOUBLE_QUOTE);
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
}
