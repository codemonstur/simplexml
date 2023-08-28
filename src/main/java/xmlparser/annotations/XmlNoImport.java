package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a field to have it be excluded from deserialization.
 * This is useful if you have a field you want to use internally
 * and you want to make sure it is never set by data.
 */
@Retention(RUNTIME)
public @interface XmlNoImport { /* just a marker */ }
