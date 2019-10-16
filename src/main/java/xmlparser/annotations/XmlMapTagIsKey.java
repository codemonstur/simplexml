package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for serializing and deserializing maps of this form:
 *
 * <map>
 *     <key>value</key>
 *     <key2>value2</key2>
 * </map>
 *
 */
@Retention(RUNTIME)
public @interface XmlMapTagIsKey {}
