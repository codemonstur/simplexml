package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Gives the field a tag of its own nested inside a tag named here, which is how a field is set
 * apart from the element holding it:
 * <pre>
 * {@code
 *     @XmlWrapperTag("wrapper")
 *     private String value;
 * }
 * </pre>
 * is written as:
 * <pre>
 * {@code
 *     <pojo><wrapper><value>something</value></wrapper></pojo>
 * }
 * </pre>
 */
@Retention(RUNTIME)
public @interface XmlWrapperTag {
    /**
     * The name of the wrapping tag the field's own element is nested inside.
     *
     * @return the tag that wraps the field's element
     */
    String value();
}
