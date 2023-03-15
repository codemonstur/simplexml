package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Sets the name of a field or class. This annotation allows you to override the
 * default name that is derived from the class or field name.
 */
@Retention(RUNTIME)
public @interface XmlName {
    String value();
}

