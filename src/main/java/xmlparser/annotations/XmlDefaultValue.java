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

    String value();
    boolean serialize() default true;
    boolean deserialize() default true;

}
