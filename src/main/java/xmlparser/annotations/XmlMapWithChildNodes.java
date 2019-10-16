package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for serializing and deserializing maps of this form:
 *
 * <item>
 *     <key>keyname</key>
 *     <value>value1</value>
 * </item>
 * <item>
 *     <key>otherkeyname</key>
 *     value2
 * </item>
 */
@Retention(RUNTIME)
public @interface XmlMapWithChildNodes {
    String keyName();
    String valueName() default "";
}
