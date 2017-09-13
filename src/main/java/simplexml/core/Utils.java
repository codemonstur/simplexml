package simplexml.core;

import simplexml.model.ObjectDeserializer;
import simplexml.model.ObjectSerializer;

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

    public static String encodeXml(final String str, final boolean encodeUTF8) {
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

}
