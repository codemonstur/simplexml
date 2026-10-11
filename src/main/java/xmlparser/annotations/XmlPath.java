package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Reads this field from somewhere other than directly under the element the object was read from.
 * The value is an XPath, evaluated against the object's own element with that element's name put
 * in front of it, so {@code @XmlPath("wrapper/value")} reaches two levels down. A path that
 * matches nothing leaves the field empty, at its zero value when the type is primitive and at
 * null otherwise.
 */
@Retention(RUNTIME)
public @interface XmlPath {
    /**
     * The path to the field's element.
     *
     * @return an XPath evaluated against the object's own element, with that element's name put
     *         in front of it
     */
    String value();
}
