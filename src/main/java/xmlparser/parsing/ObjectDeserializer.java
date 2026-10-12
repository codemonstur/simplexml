package xmlparser.parsing;

import xmlparser.model.XmlElement;

import java.util.HashMap;
import java.util.Map;

import static xmlparser.utils.Reflection.toObjectClass;

/**
 * Turns XML back into a value: the text of an element, or the value of an attribute, into an
 * object of some type. Deserializers are registered per type on the parser, and the ones
 * {@link #defaultDeserializers()} supplies cover the simple types. Write your own for a type the
 * library has no way of working out for itself.
 */
public interface ObjectDeserializer {
    /**
     * Used for turning attributes and tag names into objects. XML that looks like this:
     *
     * &lt;tag attribute=&quot;value&quot;&gt;&lt;/tag&gt;
     *
     * and also:
     *
     * &lt;map&gt;
     *     &lt;value&gt;something&lt;/value&gt;
     * &lt;/map&gt;
     *
     * @param value the value to convert
     * @return the object, null is allowed
     */
    Object convert(String value);

    /**
     * Used in most cases when we have an Element and we need to convert it into an Object.
     * The default implementation takes the text node and calls the convert(String) method. But if you want
     * to convert something special you will probably want to override this method.
     * @param element the XML element to convert
     * @return the object, null is allowed
     */
    default Object convert(XmlElement element) {
        if (element == null) return null;
        final String text = element.getText();
        if (text == null) return null;
        return convert(text);
    }
    /**
     * Converts the element through {@link #convert(XmlElement)} and gives the result back as the
     * given type, boxing the class first when it is primitive. A null element or missing text
     * stays null.
     * @param <T> the type the value should come back as
     * @param element the XML element to convert
     * @param clazz the class to cast the converted value to
     * @return the converted value, null is allowed
     * @throws ClassCastException if the value is not an instance of clazz
     */
    default <T> T convert(XmlElement element, Class<T> clazz) {
        return toObjectClass(clazz).cast(convert(element));
    }

    /**
     * Gives the deserializers the library ships with: String, Object as an identity, and the
     * primitive and boxed forms of int, long, short, byte, float, double, char and boolean. It is
     * the map a parser starts from, before any deserializer of your own is registered.
     * @return a fresh map, safe to add to
     */
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
        deserializers.put(Character.class, ObjectDeserializer::toCharacter);
        deserializers.put(char.class, ObjectDeserializer::toCharacter);
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

    /**
     * Takes the character text holds, or gives back nothing when the text is empty: an attribute
     * or an element holding no characters is no value for a char field, so it reads the way a
     * missing one does rather than failing on a character that is not there.
     *
     * @param value the text to take the character from
     * @return the first character of the text, or null when it holds none
     */
    static Character toCharacter(final String value) {
        return value.isEmpty() ? null : value.charAt(0);
    }

}
