package simplexml.model;

import java.util.HashMap;
import java.util.Map;

import static simplexml.utils.Reflection.toObjectClass;

public interface ObjectDeserializer {
    Object convert(String value);
    default <T> T convert(String value, Class<T> clazz) {
        return toObjectClass(clazz).cast(convert(value));
    }

    static Map<Class<?>, ObjectDeserializer> defaultDeserializers() {
        final Map<Class<?>, ObjectDeserializer> deserializers = new HashMap<>();
        deserializers.put(Integer.class, Integer::valueOf);
        deserializers.put(int.class, Integer::valueOf);
        deserializers.put(Double.class, Double::valueOf);
        deserializers.put(double.class, Double::valueOf);
        deserializers.put(Float.class, Float::valueOf);
        deserializers.put(float.class, Float::valueOf);
        deserializers.put(Byte.class, Byte::valueOf);
        deserializers.put(byte.class, Byte::valueOf);
        deserializers.put(Character.class, value -> (value.charAt(0)));
        deserializers.put(char.class, value -> (value.charAt(0)));
        deserializers.put(Long.class, Long::valueOf);
        deserializers.put(long.class, Long::valueOf);
        deserializers.put(Short.class, Short::valueOf);
        deserializers.put(short.class, Short::valueOf);
        deserializers.put(String.class, value -> value);
        deserializers.put(boolean.class, Boolean::valueOf);
        deserializers.put(Boolean.class, Boolean::valueOf);
        deserializers.put(Object.class, value -> value);
        return deserializers;
    }

}
