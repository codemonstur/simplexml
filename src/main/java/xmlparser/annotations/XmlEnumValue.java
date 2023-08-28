package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Changes an enum value to some other value. Example:
 * <pre>
 * {@code
 *     class Pojo {
 *         MyEnum value;
 *     }
 *
 *     enum MyEnum {
 *         one,
 *         \@XmlEnumValue("123")
 *         two
 *     }
 * }
 * </pre>
 * The above POJO with 'value' set to 'one' will serialize into
 * <pre>
 * {@code
 *     <pojo><value>one</value></pojo>
 * }
 * </pre>
 * The same POJO with 'value' set to 'two' will serialize into
 * <pre>
 * {@code
 *     <pojo><value>123</value></pojo>
 * }
 * </pre>
 * This annotation is also respected during deserialization.
 */
@Retention(RUNTIME)
public @interface XmlEnumValue {
    String value();
}
