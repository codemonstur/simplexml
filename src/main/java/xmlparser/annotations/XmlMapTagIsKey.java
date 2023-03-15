package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for serializing and deserializing maps of this form:
 * <pre>
 * {@code
 * <map>
 *     <key>value</key>
 *     <key2>value2</key2>
 * </map>
 * }
 * </pre>
 *
 * Be aware that xml tag names cannot be encoded and have strict rules about
 * acceptable naming. If your map contains keys that are not valid XML tag
 * names you will generate invalid XML.
 */
@Retention(RUNTIME)
public @interface XmlMapTagIsKey {}
