package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Names a static method to call for this field, for a shape the parser cannot work out by itself.
 * The method takes the XmlElement that would have been read for the field and returns whatever
 * belongs in it, and a method that cannot be found or called is reported as InvalidAnnotation.
 * <pre>
 * {@code
 *     class Pojo {
 *         @XmlFieldDeserializer(clazz="com.example.Shapes", function="readCircles")
 *         private Map<String, Circle> circles;
 *     }
 * }
 * </pre>
 */
@Retention(RUNTIME)
public @interface XmlFieldDeserializer {
    /**
     * The class holding the deserializer method.
     *
     * @return the fully qualified name to look the method up on
     */
    String clazz();
    /**
     * The name of the static method on {@code clazz()} to call for the field.
     *
     * @return the method, taking the XmlElement read for the field and returning what belongs in
     *         it
     */
    String function();
}
