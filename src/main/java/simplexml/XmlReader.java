package simplexml;

import simplexml.annotations.XmlAbstractClass;
import simplexml.annotations.XmlNoImport;
import simplexml.error.InvalidXml;
import simplexml.model.*;
import simplexml.parsing.DomBuilder;
import simplexml.parsing.EventParser;
import simplexml.parsing.ObjectDeserializer;
import simplexml.utils.Interfaces.AccessDeserializers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static org.objenesis.ObjenesisHelper.newInstance;
import static simplexml.model.Element.findChildForName;
import static simplexml.utils.Constants.*;
import static simplexml.utils.Functions.trim;
import static simplexml.utils.Reflection.*;
import static simplexml.utils.XML.unescapeXml;

public interface XmlReader extends AccessDeserializers {

    default <T> T domToObject(final Element node, final Class<T> clazz) throws IllegalAccessException {
        if (node == null) return null;
        final ObjectDeserializer c = getDeserializer(clazz);
        if (c != null) return c.convert(node, clazz);

        final T o = newInstance(clazz);

        for (final Field f : listFields(clazz)) {
            f.setAccessible(true);
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (f.isAnnotationPresent(XmlNoImport.class)) continue;

            switch (toFieldType(f)) {
                case TEXTNODE: f.set(o, textNodeToValue(f.getType(), node)); break;
                case ANNOTATED_ATTRIBUTE: f.set(o, attributeToValue(f.getType(), toName(f), deWrap(node, f))); break;
                case SET: f.set(o, domToSet(f, toClassOfCollection(f), toName(f), deWrap(node, f))); break;
                case LIST: f.set(o, domToList(f, toClassOfCollection(f), toName(f), deWrap(node, f))); break;
                case ARRAY: f.set(o, domToArray(f.getType().getComponentType(), toName(f), deWrap(node, f))); break;
                case MAP: f.set(o, domToMap((ParameterizedType) f.getGenericType(), toName(f), deWrap(node, f))); break;
                default:
                    final String name = toName(f);
                    final String value = node.attributes.get(name);
                    if (value != null) {
                        f.set(o, stringToValue(f.getType(), value));
                        break;
                    }
                    if (isAbstract(f)) {
                        final Element child = node.findChildForName(name, null);
                        f.set(o, domToObject(child, findAbstractType(f.getAnnotation(XmlAbstractClass.class), child)));
                        break;
                    }
                    f.set(o, domToObject(findChildForName(deWrap(node, f),name, null), f.getType()));
                    break;
            }
        }
        return o;
    }

    default Element deWrap(final Element element, final Field field) {
        if (!isWrapped(field)) return element;
        return element.findChildForName(toWrappedName(field), null);
    }
    default Object textNodeToValue(final Class<?> type, final Element node) {
        final ObjectDeserializer conv = getDeserializer(type);
        return (conv != null) ? conv.convert(node) : null;
    }
    default Object attributeToValue(final Class<?> type, final String name, final Element node) {
        final ObjectDeserializer conv = getDeserializer(type);
        if (conv == null) return null;
        final String value = node.attributes.get(name);
        if (value == null) return null;
        return conv.convert(value);
    }
    default Object stringToValue(final Class<?> type, final String value) {
        final ObjectDeserializer conv = getDeserializer(type);
        return (conv != null) ? conv.convert(value) : null;
    }
    default Set<Object> domToSet(final Field field, final Class<?> type, final String name, final Element node) throws IllegalAccessException {
        if (node == null) return null;
        final ObjectDeserializer elementConv = getDeserializer(type);
        final boolean isAbstract = isAbstract(field);

        final Set<Object> set = new HashSet<>();
        for (final Element n : node.children) {
            if (!n.name.equals(name)) continue;
            if (isAbstract) {
                set.add(domToObject(n, findAbstractType(field.getAnnotation(XmlAbstractClass.class), n)));
                continue;
            }

            set.add( (elementConv == null) ? domToObject(n, type) : elementConv.convert(n));
        }
        return set;
    }
    default List<Object> domToList(final Field field, final Class<?> type, final String name, final Element node) throws IllegalAccessException {
        if (node == null) return null;
        final ObjectDeserializer elementConv = getDeserializer(type);
        final boolean isAbstract = isAbstract(field);

        final List<Object> list = new LinkedList<>();
        for (final Element n : node.children) {
            if (!n.name.equals(name)) continue;
            if (isAbstract) {
                list.add(domToObject(n, findAbstractType(field.getAnnotation(XmlAbstractClass.class), n)));
                continue;
            }

            list.add( (elementConv == null) ? domToObject(n, type) : elementConv.convert(n));
        }
        return list;
    }
    default Object[] domToArray(final Class<?> type, final String name, final Element node) throws IllegalAccessException {
        if (node == null) return null;
        final ObjectDeserializer elementConv = getDeserializer(type);

        final Object[] array = (Object[]) Array.newInstance(type, node.numChildrenWithName(name));
        int i = 0;
        for (final Element n : node.children) {
            if (n.name.equals(name)) {
                array[i] = (elementConv == null) ? domToObject(n, type) : elementConv.convert(n, type);
                i++;
            }
        }
        return array;
    }
    default Map<Object, Object> domToMap(final ParameterizedType type, final String name, final Element node) {
        if (node == null) return null;
        final Element element = node.findChildForName(name, null);
        if (element == null) return null;

        final ObjectDeserializer convKey = getDeserializer(toClassOfMapKey(type));
        final ObjectDeserializer convVal = getDeserializer(toClassOfMapValue(type));

        final Map<Object, Object> map = new HashMap<>();
        for (final Element child : element.children) {
            map.put(convKey.convert(child.name), convVal.convert(child));
        }
        return map;
    }

    static Element parseXML(final InputStreamReader in) throws IOException {
        final DomBuilder p = new DomBuilder();
        parseXML(in, p);
        return p.getRoot();
    }

    static void parseXML(final InputStreamReader in, final EventParser parser) throws IOException {
        String str;
        while ((str = readLine(in, XML_TAG_START)) != null) {
            if (!str.isEmpty()) parser.someText(unescapeXml(str.trim()));

            str = trim(readLine(in, XML_TAG_END));
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            if (str.charAt(0) == XML_PROLOG) continue;

            if (str.charAt(0) == XML_SELF_CLOSING) parser.endNode();
            else {
                final String name = getNameOfTag(str);
                if (str.length() == name.length()) {
                    parser.startNode(str, new HashMap<>());
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();
                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, parseAttributes(str.substring(beginAttr, end-1)));
                    parser.endNode();
                } else {
                    parser.startNode(name, parseAttributes(str.substring(beginAttr+1, end)));
                }
            }
        }
    }


    static String readLine(final InputStreamReader in, final char end) throws IOException {
        final List<Character> chars = new LinkedList<>();
        int data;
        while ((data = in.read()) != -1) {
            if (data == end) break;
            chars.add((char) data);
        }
        if (data == -1) return null;

        char[] value = new char[chars.size()];
        int i = 0;
        for (final Character c : chars) value[i++] = c;
        return new String(value);
    }

    static String getNameOfTag(final String tag) {
        int offset = 0;
        for (; offset < tag.length(); offset++) {
            if (tag.charAt(offset) == CHAR_SPACE || tag.charAt(offset) == CHAR_FORWARD_SLASH)
                break;
        }
        return tag.substring(0, offset);
    }

    static HashMap<String, String> parseAttributes(String input) {
        final HashMap<String, String> attributes = new HashMap<>();

        while (!input.isEmpty()) {
            int startName = indexOfNonWhitespaceChar(input, 0);
            if (startName == -1) break;
            int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) break;

            final String name = input.substring(startName, equals).trim();
            input = input.substring(equals+1);

            int startValue = indexOfNonWhitespaceChar(input, 0);
            if (startValue == -1) break;

            int endValue; final String value;
            if (input.charAt(startValue) == CHAR_DOUBLE_QUOTE) {
                startValue++;
                endValue = input.indexOf(CHAR_DOUBLE_QUOTE, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue).trim();
            } else {
                endValue = indexOfWhitespaceChar(input, startValue+1);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue+1).trim();
            }

            input = input.substring(endValue+1);

            attributes.put(name, unescapeXml(value));
        }

        return attributes;
    }

    static int indexOfNonWhitespaceChar(final String input, final int offset) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (at == ' ' || at == '\t' || at == '\n' || at == '\r') continue;
            return i;
        }
        return -1;
    }
    static int indexOfWhitespaceChar(final String input, final int offset) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (at == ' ' || at == '\t' || at == '\n' || at == '\r') return i;
        }
        return -1;
    }
}
