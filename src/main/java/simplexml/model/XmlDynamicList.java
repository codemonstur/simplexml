package simplexml.model;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * Should deal with lists that have a wrapper tag and the name of the item determines the
 * type of the item. So:
 *
 * <list>
 *     <string>String here</string>
 *     <double>2.5</double>
 * </list>
 *
 * We want to turn that into; List<Object> list
 */
@Retention(RUNTIME)
public @interface XmlDynamicList {
    @interface TypeMap {
        String name();
        Class<?> type();
    }

    String name();
    TypeMap[] types();
}
