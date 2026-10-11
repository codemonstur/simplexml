package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for deserializing fields of abstract classes
 */
@Retention(RUNTIME)
public @interface XmlAbstractClass {

    /**
     * Pairs one value of the type marker with the class it stands for: what was read out of the
     * marker attribute or tag, and the type to build from it.
     */
    @interface TypeMap {
        /**
         * The marker value this pair stands for.
         *
         * @return the text that selects this type, as it appears in the attribute or tag
         */
        String name();
        /**
         * The concrete class to build for this pair.
         *
         * @return the type to instantiate when the marker holds {@code name()}
         */
        Class<?> type();
    }

    /**
     * The name of the attribute holding the type marker.
     *
     * @return the attribute to read it from, unless {@code tag()} names a child element instead;
     *         it defaults to {@code "class"}
     */
    String attribute() default "class";
    /**
     * The name of a child element holding the type marker.
     *
     * @return the child element to read it from instead of the attribute; the empty default means
     *         {@code attribute()} is used
     */
    String tag() default "";
    /**
     * The pairs mapping a marker value to the class it stands for.
     *
     * @return every type this annotation can build; a marker matching none of them is an
     *         InvalidAnnotation
     */
    TypeMap[] types();
}
