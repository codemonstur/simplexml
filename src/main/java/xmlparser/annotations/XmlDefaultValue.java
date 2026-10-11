package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be used to set a default value for a field in the event it is null.
 *
 * The annotation works during both serialization and deserialization.
 *
 * During serialization the parser will check if the field in the object is null and write
 * the default instead.
 *
 * During deserialization the parser will check if the field will be set to null. If yes it
 * will set the default instead.
 */
@Retention(RUNTIME)
public @interface XmlDefaultValue {

    /**
     * The default as text.
     *
     * @return what is converted to the field's type when read, and written as-is when the field
     *         is null
     */
    String value();
    /**
     * Whether the default is written when the field holds null.
     *
     * @return false to leave a null field out of the document entirely
     */
    boolean serialize() default true;
    /**
     * Whether the default is applied when the document leaves the field null.
     *
     * @return false to let the field simply stay null
     */
    boolean deserialize() default true;

}
