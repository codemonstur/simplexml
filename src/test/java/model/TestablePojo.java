package model;

import xmlparser.annotations.XmlObjectValidator;

public final class TestablePojo {

    public final String name;

    public TestablePojo() {
        this.name = null;
    }

    public TestablePojo(String name) {
        this.name = name;
    }

    @XmlObjectValidator
    public void validate() {
        if ("invalid".equals(name))
            throw new IllegalArgumentException("Name is 'invalid'");
    }

}
