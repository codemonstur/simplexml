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

    static ObjectSerializer defaultSerializer() {
        return Object::toString;
    }
}

