package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a field as the textnode of a class.
 */
@Retention(RUNTIME)
public @interface XmlTextNode {
    /**
     * A regular expression the read text must match in full.
     *
     * @return the pattern, or the empty string for none; text that does not match is an InvalidXml
     */
    String pattern() default "";

    /**
     * Whether the text node is written as a CDATA section.
     *
     * @return true to wrap the text in {@code <![CDATA[...]]>}, false to escape it normally
     */
    boolean cdata() default false;
}
