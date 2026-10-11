package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Not yet implemented. Just here for future use
 */
@Retention(RUNTIME)
public @interface XmlValidate {

    /**
     * Reserved: whether unknown tags would be tolerated.
     *
     * @return nothing reads this yet
     */
    boolean allowExtraTags() default false;
    /**
     * Reserved: whether missing tags would be tolerated.
     *
     * @return nothing reads this yet
     */
    boolean allowMissingTags() default false;
    /**
     * Reserved: the missing tags that would be tolerated by name.
     *
     * @return nothing reads this yet
     */
    String[] allowedMissingTags();
    /**
     * Reserved: the extra tags that would be tolerated by name.
     *
     * @return nothing reads this yet
     */
    String[] allowedExtraTags();

}
