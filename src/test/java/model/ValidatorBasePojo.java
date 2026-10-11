package model;

import xmlparser.annotations.XmlObjectValidator;

public class ValidatorBasePojo {

    public final String name;

    public ValidatorBasePojo() {
        this.name = null;
    }

    public ValidatorBasePojo(final String name) {
        this.name = name;
    }

    @XmlObjectValidator
    public void validate() {
        if ("invalid".equals(name))
            throw new IllegalArgumentException("Name is 'invalid'");
    }

}
