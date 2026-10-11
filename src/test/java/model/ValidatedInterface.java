package model;

import xmlparser.annotations.XmlObjectValidator;

public interface ValidatedInterface {

    String name();

    @XmlObjectValidator
    default void validate() {
        if ("invalid".equals(name()))
            throw new IllegalArgumentException("Name is 'invalid'");
    }

}
