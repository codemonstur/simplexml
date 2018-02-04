package simplexml.core;

import simplexml.model.ObjectDeserializer;
import simplexml.model.ObjectSerializer;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static simplexml.core.Constants.*;

public enum Utils {;

    public interface AccessSerializers {
        boolean hasSerializer(Class<?> type);
        ObjectSerializer getSerializer(Class<?> type);
    }
    public interface AccessDeserializers {
        ObjectDeserializer getDeserializer(Class<?> type);
    }
    public interface ParserConfiguration {
        boolean shouldEncodeUTF8();
    }

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

    public static <T> T newObject(final Class<T> clazz, Class<? super T> parent) {
        try {
            // Call the declared no-args constructor
            final Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            return constructor.newInstance(new Object[0]);
        } catch (Exception e) {
            try {
                // create a no-args empty constructor if something went wrong
                return clazz.cast(ReflectionFactory.getReflectionFactory().newConstructorForSerialization(clazz,
                        parent.getDeclaredConstructor()).newInstance());
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot create object", ex);
            }
        }
    }
}
