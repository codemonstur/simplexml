package simplexml.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface XmlFieldDeserializer {
    String clazz();
    String function();
}
