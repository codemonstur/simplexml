package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Instructs the parser to ignore the field name and use the name from the class.
 * The parser will also honor the @XmlName annotation on the class.
 * <pre>
 * {@code
 *     class Envelope {
 *         @XmlNameFromClass
 *         public Object body = new MyBody();
 *     }
 *
 *     @XmlName("custom")
 *     class MyBody {
 *         ...
 *     }
 * }
 * </pre>
 * Serializes into:
 * <pre>
 * {@code
 *     <Envelope><custom></custom></Envelope>
 * }
 * </pre>
 */
@Retention(RUNTIME)
public @interface XmlNameFromClass {
}
