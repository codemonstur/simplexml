package model;

public final class ValidatorInterfacePojo implements ValidatedInterface {

    public final String name;

    public ValidatorInterfacePojo() {
        this.name = null;
    }

    public ValidatorInterfacePojo(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }

}
