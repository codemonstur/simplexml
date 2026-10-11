package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for serializing and deserializing maps of this form:
 * <pre>
 * {@code
 * <item>
 *     <key>keyname</key>
 *     <value>value1</value>
 * </item>
 * <item>
 *     <key>otherkeyname</key>
 *     value2
 * </item>
 * }
 * </pre>
 */
@Retention(RUNTIME)
public @interface XmlMapWithChildNodes {
    /**
     * The name of the child element of each item that holds the map key.
     *
     * @return the child element to read each key from
     */
    String keyName();
    /**
     * The name of the child element holding the map value.
     *
     * @return the child element to read each value from; the empty default means the whole item
     *         element is the value
     */
    String valueName() default "";
}
