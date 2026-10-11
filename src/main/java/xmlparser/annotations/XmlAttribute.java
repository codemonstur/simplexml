package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Instructs the parser to treat the field as an attribute in XML. This
 * annotation works for both serialization as deserialization
 * <pre>
 * {@code
 *     class Pojo {
 *         @XmlAttribute
 *         public String name = "Hello";
 *     }
 * }
 * </pre>
 * Turns into:
 * <pre>
 * {@code
 *     <Pojo name="Hello"></Pojo>
 * }
 * </pre>
 */
@Retention(RUNTIME)
public @interface XmlAttribute {
    /**
     * A regular expression the read attribute value must match in full.
     *
     * @return the pattern, or the empty string for none; a value that does not match is an
     *         InvalidXml
     */
    String pattern() default "";
}
