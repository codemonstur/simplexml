package xmlparser.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Not yet implemented. Just here for future use
 */
@Retention(RUNTIME)
public @interface XmlValidate {

    boolean allowExtraTags() default false;
    boolean allowMissingTags() default false;
    String[] allowedMissingTags();
    String[] allowedExtraTags();

}
