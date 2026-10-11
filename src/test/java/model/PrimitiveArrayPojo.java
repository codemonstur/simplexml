package model;

public final class PrimitiveArrayPojo {

    public int[] numbers;
    public boolean[] booleans;
    public String[] strings;

    public PrimitiveArrayPojo() {}

    public PrimitiveArrayPojo(final int[] numbers, final boolean[] booleans, final String[] strings) {
        this.numbers = numbers;
        this.booleans = booleans;
        this.strings = strings;
    }

}
