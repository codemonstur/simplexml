package simplexml.utils;

import simplexml.model.XmlName;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static simplexml.utils.Constants.*;

public enum Functions {;

    public static String escapeXml(final String str, final boolean encodeUTF8) {
        if (str == null) return null;
        if (str.isEmpty()) return str;

        final StringBuffer encoded = new StringBuffer();
        for (final char c : str.toCharArray()) {
            switch (c) {
                case CHAR_LESS_THAN:
                    encoded.append(ENCODED_LESS_THAN); break;
                case CHAR_DOUBLE_QUOTE:
                    encoded.append(ENCODED_DOUBLE_QUOTE); break;
                case CHAR_GREATER_THAN:
                    encoded.append(ENCODED_GREATER_THAN); break;
                case CHAR_SINGLE_QUOTE:
                    encoded.append(ENCODED_SINGLE_QUOTE); break;
                case CHAR_AMPERSAND:
                    encoded.append(ENCODED_AMPERSAND); break;
                default:
                    encoded.append( (encodeUTF8 && c > 0x7e) ? AMPERSAND+HASH+((int)c)+SEMICOLON : c);
                    break;
            }
        }

        return encoded.toString();
    }

    public static String unescapeXml(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
            } else {
                if (text.startsWith(ENCODED_AMPERSAND, i)) {
                    result.append(CHAR_AMPERSAND);
                    i += 5;
                } else if (text.startsWith(ENCODED_SINGLE_QUOTE, i)) {
                    result.append(CHAR_SINGLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_DOUBLE_QUOTE, i)) {
                    result.append(CHAR_DOUBLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_LESS_THAN, i)) {
                    result.append(CHAR_LESS_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_GREATER_THAN, i)) {
                    result.append(CHAR_GREATER_THAN);
                    i += 4;
                } else i++;
            }
        }
        return result.toString();
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
    public static <T> Constructor<?> newNoArgsConstructor(final Class<T> clazz, final Class<? super T> parent) throws
            NoSuchMethodException {
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
