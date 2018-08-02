package simplexml.model;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * Used for deserializing fields of abstract classes
 */
@Retention(RUNTIME)
public @interface XmlAbstractClass {

    @interface TypeMap {
        String name();
        Class<?> type();
    }

    String attribute() default "class";
    String tag() default "";
    TypeMap[] types();
}
