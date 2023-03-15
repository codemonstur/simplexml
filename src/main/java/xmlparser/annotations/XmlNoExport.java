package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a field to have it be excluded from serialization.
 */
@Retention(RUNTIME)
public @interface XmlNoExport { /* just a marker */ }
