package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a no-argument method as a check to run on an object once it has been read and before it
 * is handed back. A method that throws means the XML parsed but the object is not acceptable, and
 * the failure is reported as InvalidObject. The whole hierarchy is searched, so a validator
 * inherited from a superclass or an interface runs too, and an override runs only once.
 */
@Retention(RUNTIME)
public @interface XmlObjectValidator {}
