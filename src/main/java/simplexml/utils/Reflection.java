package simplexml.utils;

import simplexml.model.*;
import simplexml.model.XmlAbstractClass.TypeMap;
import simplexml.utils.Interfaces.AccessSerializers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static java.lang.String.format;
import static simplexml.model.Element.findChildForName;
import static simplexml.utils.Reflection.ClassType.*;

public enum Reflection {;

    public static Field determineTypeOfFields(final Class<?> clazz, final Object o, final List<Field> attributes
            , final List<Field> childNodes) throws IllegalAccessException {
        Field textNode = null;
        for (final Field f : listFields(clazz)) {
            f.setAccessible(true);
            if (f.get(o) == null) continue;
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (Modifier.isTransient(f.getModifiers())) continue;
            if (f.isAnnotationPresent(XmlNoExport.class)) continue;
            if (f.isAnnotationPresent(XmlTextNode.class))
                textNode = f;
            else if (f.isAnnotationPresent(XmlAttribute.class))
                attributes.add(f);
            else
                childNodes.add(f);
        }
        return textNode;
    }

    public enum FieldType {
        TEXTNODE, ANNOTATED_ATTRIBUTE, SET, LIST, ARRAY, MAP, OTHER
    }
    public static FieldType toFieldType(final Field f) {
        if (f.isAnnotationPresent(XmlTextNode.class)) return FieldType.TEXTNODE;
        if (f.isAnnotationPresent(XmlAttribute.class)) return FieldType.ANNOTATED_ATTRIBUTE;

        final Class<?> type = f.getType();
        if (Set.class.isAssignableFrom(type)) return FieldType.SET;
        if (List.class.isAssignableFrom(type)) return FieldType.LIST;
        if (type.isArray()) return FieldType.ARRAY;
        if (Map.class.isAssignableFrom(type)) return FieldType.MAP;
        return FieldType.OTHER;
    }

    public enum ClassType {
        SIMPLE, ARRAY, LIST, SET, MAP, OBJECT
    }
    public static ClassType toClassType(final Class<?> c, final AccessSerializers s) {
        if (isSimple(c) || s.hasSerializer(c)) return SIMPLE;
        if (c.isArray()) return ARRAY;
        if (isList(c)) return LIST;
        if (isSet(c)) return SET;
        if (isMap(c)) return MAP;
        return OBJECT;
    }

    public static List<Field> listFields(final Class<?> type) {
        return listFields(new ArrayList<>(), type);
    }
    public static List<Field> listFields(final List<Field> fields, final Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        if (type.getSuperclass() != null) listFields(fields, type.getSuperclass());
        return fields;
    }

    public static boolean isSimple(final Class<?> c) {
        return c.isAssignableFrom(Double.class)
            || c.isAssignableFrom(double.class)
            || c.isAssignableFrom(Integer.class)
            || c.isAssignableFrom(String.class)
            || c.isAssignableFrom(int.class)
            || c.isAssignableFrom(float.class)
            || c.isAssignableFrom(Float.class)
            || c.isAssignableFrom(byte.class)
            || c.isAssignableFrom(Byte.class)
            || c.isAssignableFrom(char.class)
            || c.isAssignableFrom(Character.class)
            || c.isAssignableFrom(short.class)
            || c.isAssignableFrom(Short.class)
            || c.isAssignableFrom(Long.class)
            || c.isAssignableFrom(long.class)
            || c.isAssignableFrom(boolean.class)
            || c.isAssignableFrom(Boolean.class);
    }
    public static boolean isList(final Class<?> c) {
        return c.isAssignableFrom(List.class);
    }
    public static boolean isSet(final Class<?> c) {
        return c.isAssignableFrom(Set.class);
    }
    public static boolean isMap(final Class<?> c) {
        return c.isAssignableFrom(Map.class);
    }
    public static boolean isWrapped(final Field f) {
        return f.isAnnotationPresent(XmlWrapperTag.class);
    }
    public static String toWrappedName(final Field f) {
        return f.getAnnotation(XmlWrapperTag.class).value();
    }
    public static boolean isAbstract(final Field f) {
        return f.isAnnotationPresent(XmlAbstractClass.class);
    }
    public static Class<?> findAbstractType(final XmlAbstractClass annotation, final Element node) throws IllegalAccessException {
        final String typeName = findAbstractTypeName(annotation, node);
        for (final TypeMap map : annotation.types()) {
            if (typeName.equals(map.name()))
                return map.type();
        }
        throw new IllegalAccessException(format("Missing type for '%s'", typeName));
    }
    private static String findAbstractTypeName(final XmlAbstractClass annotation, final Element node) throws IllegalAccessException {
        if (!annotation.tag().isEmpty()) {
            final Element child = findChildForName(node, annotation.tag(), null);
            if (child == null) throw new IllegalAccessException(format("Missing tag %s in element %s", annotation.tag(), node.name));
            return child.text;
        }
        else return node.attributes.get(annotation.attribute());
    }

    public static String toName(final Class<?> o) {
        if (!o.isAnnotationPresent(XmlName.class))
            return o.getSimpleName().toLowerCase();
        return o.getAnnotation(XmlName.class).value();
    }

    public static String toName(final Field field) {
        if (field.isAnnotationPresent(XmlName.class))
            return field.getAnnotation(XmlName.class).value();
        return field.getName();
    }

    public static Class<?> toClassOfCollection(final Field f) {
        final ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }
    public static Class<?> toClassOfMapKey(final ParameterizedType type) {
        return (Class<?>)type.getActualTypeArguments()[0];
    }
    public static Class<?> toClassOfMapValue(final ParameterizedType type) {
        return (Class<?>)type.getActualTypeArguments()[1];
    }

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TO_OBJECT = primitiveToObject();
    private static Map<Class<?>, Class<?>> primitiveToObject() {
        final Map<Class<?>, Class<?>> classes = new HashMap<>();
        classes.put(boolean.class, Boolean.class);
        classes.put(byte.class, Byte.class);
        classes.put(char.class, Character.class);
        classes.put(double.class, Double.class);
        classes.put(float.class, Float.class);
        classes.put(int.class, Integer.class);
        classes.put(long.class, Long.class);
        classes.put(short.class, Short.class);
        classes.put(void.class, Void.class);
        return classes;
    }
    public static <T> Class<T> toObjectClass(final Class<T> clazz) {
        return clazz.isPrimitive() ? (Class<T>) PRIMITIVE_TO_OBJECT.get(clazz) : clazz;
    }

}
