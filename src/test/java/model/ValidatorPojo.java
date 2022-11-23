package model;

import xmlparser.annotations.XmlObjectValidator;

public final class ValidatorPojo {

    public final String name;

    public ValidatorPojo() {
        this.name = null;
    }

    public ValidatorPojo(String name) {
        this.name = name;
    }

    @XmlObjectValidator
    public void validate() {
        if ("invalid".equals(name))
            throw new IllegalArgumentException("Name is 'invalid'");
    }

}
