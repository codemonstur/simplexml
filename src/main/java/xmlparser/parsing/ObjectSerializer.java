package xmlparser.parsing;

/**
 * Converts an object of a given type into XML.
 * ObjectSerializer's are used by XmlWriter to write actual XML. The XmlWriter will
 * lookup the proper serializer for a given type, and you can define your own when
 * you want.
 */
public interface ObjectSerializer {
    /**
     * Performs the actual conversion.
     * @param value The object to turn into XML
     * @return The XML code
     */
    String convert(Object value);

    /**
     * Gives the serializer a parser starts from, used for any type without one of its own: it
     * turns the object into text with its {@code toString()}.
     * @return the fall-back serializer
     */
    static ObjectSerializer defaultSerializer() {
        return Object::toString;
    }
}

