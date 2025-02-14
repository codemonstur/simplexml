package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Uses the selected default value in case there is no match. Example:
 * Assuming the following XML input and class definitions
 * <pre>
 * {@code
 *     <pojo><value>three</value></pojo>
 * }
 * {@code
 *     class Pojo {
 *         MyEnum value;
 *     }
 *
 *     enum MyEnum {
 *         one,
 *         \@XmlEnumDefaultValue
 *         two
 *     }
 * }
 * </pre>
 * Will deserialize into an object with value 'two' for the field value.
 * This annotation has no effect during serialization.
 */
@Retention(RUNTIME)
public @interface XmlEnumDefaultValue { /* just a marker */ }
