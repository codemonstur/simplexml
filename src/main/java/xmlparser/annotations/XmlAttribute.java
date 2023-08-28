package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Instructs the parser to treat the field as an attribute in XML. This
 * annotation works for both serialization as deserialization
 * <pre>
 * {@code
 *     class Pojo {
 *         \@XmlAttribute
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
    String pattern() default "";
}
