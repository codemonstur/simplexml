package simplexml.utils;

import simplexml.model.XmlAttribute;
import simplexml.model.XmlName;
import simplexml.model.XmlNoExport;
import simplexml.model.XmlTextNode;
import simplexml.utils.Interfaces.AccessSerializers;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static simplexml.utils.Reflection.ClassType.*;

public enum Reflection {;

    public static Field determineTypeOfFields(final Class<?> clazz, final Object o, final List<Field> attributes
            , final List<Field> childNodes) throws IllegalAccessException {
        Field textNode = null;
        for (final Field f : clazz.getDeclaredFields()) {
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

    public static <T> T newObject(final Class<T> clazz) {
        return newObject(clazz, Object.class);
    }

    public static <T> T newObject(final Class<T> clazz, final Class<? super T> parent) {
        try {
            return toDeclaredNoArgsConstructor(clazz).newInstance();
        } catch (Exception e) {
            try {
                return clazz.cast(newNoArgsConstructor(clazz, parent).newInstance());
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot create object", ex);
            }
        }
    }

    public static <T> Constructor<T> toDeclaredNoArgsConstructor(final Class<T> clazz) throws NoSuchMethodException {
        final Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor;
    }
    public static <T> Constructor<?> newNoArgsConstructor(final Class<T> clazz, final Class<? super T> parent)
            throws NoSuchMethodException {
        return ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz,
                parent.getDeclaredConstructor());
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

}
