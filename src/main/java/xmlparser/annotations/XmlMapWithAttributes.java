package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for serializing and deserializing maps of this form:
 * <pre>
 * {@code
 * <item key="key">value</item>
 * <item key="key2" value="value2" />
 * }
 * </pre>
 */
@Retention(RUNTIME)
public @interface XmlMapWithAttributes {
    /**
     * The name of the attribute on each item element that holds the map key.
     *
     * @return the attribute to read each key from
     */
    String keyName();
    /**
     * The name of the attribute holding the map value.
     *
     * @return the attribute to read each value from; the empty default means the value is the
     *         item element's text instead
     */
    String valueName() default "";
}
