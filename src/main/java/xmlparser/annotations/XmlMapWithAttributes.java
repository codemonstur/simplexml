package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * Used for serializing and deserializing maps of this form:
 *
 * <item key="key">value</item>
 * <item key="key2" value="value2" />
 */
@Retention(RUNTIME)
public @interface XmlMapWithAttributes {
    String keyName();
    String valueName() default "";
}
