package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a field as the textnode of a class.
 */
@Retention(RUNTIME)
public @interface XmlTextNode { /* just a marker */ }
