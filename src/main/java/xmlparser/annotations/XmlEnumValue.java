package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * Changes an enum value to some other value. Example:
 *
 *     class Pojo {
 *         MyEnum value;
 *     }
 *
 *     enum MyEnum {
 *         one,
 *         @XmlEnumValue("123")
 *         two
 *     }
 *
 * The above POJO with 'value' set to 'one' will serialize into
 *
 *     <pojo><value>one</value></pojo>
 *
 * The same POJO with 'value' set to 'two' will serialize into
 *
 *     <pojo><value>123</value></pojo>
 *
 * This annotation is also respected during deserialization.
 */
@Retention(RUNTIME)
public @interface XmlEnumValue {
    String value();
}
