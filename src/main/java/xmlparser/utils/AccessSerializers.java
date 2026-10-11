package xmlparser.utils;

import xmlparser.parsing.ObjectSerializer;

/**
 * Effectively declares the getters that let XmlWriter find the serializer for a type, the
 * writing side of AccessDeserializers. If you want a Writer with some fancy way of retrieving
 * serializers you can write your own implementation of this interface. You probably shouldn't.
 */
public interface AccessSerializers {
    /**
     * Used to check whether a serializer is available for a type.
     * @param type the type we are trying to serialize
     * @return true when a serializer is available
     */
    boolean hasSerializer(Class<?> type);
    /**
     * Used to get a serializer for a type.
     * @param type the type we are trying to serialize
     * @return a serializer that works
     */
    ObjectSerializer getSerializer(Class<?> type);
}
