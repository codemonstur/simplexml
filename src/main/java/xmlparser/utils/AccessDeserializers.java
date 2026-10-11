package xmlparser.utils;

import xmlparser.parsing.ObjectDeserializer;

/**
 * Effectively declares a getter to allow XmlReader to get the deserializers for a type.
 * If you want to design your own Reader that has some fancy way of retrieving
 * deserializers you can write your own implementation of this interface. You probably
 * shouldn't.
 */
public interface AccessDeserializers {
    /**
     * Used to get a deserializer for a type.
     * @param type the type we are trying to deserialize
     * @return a deserializer that works
     */
    ObjectDeserializer getDeserializer(Class<?> type);
}
