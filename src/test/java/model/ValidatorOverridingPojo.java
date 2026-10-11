package model;

import xmlparser.annotations.XmlObjectValidator;

public final class ValidatorOverridingPojo extends ValidatorBasePojo {

    public ValidatorOverridingPojo() {
        super();
    }

    public ValidatorOverridingPojo(final String name) {
        super(name);
    }

    @Override
    @XmlObjectValidator
    public void validate() {
        if ("overriding".equals(name))
            throw new IllegalArgumentException("Name is 'overriding'");
        super.validate();
    }

}
