package model;

public final class SimplePojo {

    public final Integer integer;
    public final Character character;
    public final String string;
    public final Byte bytes;
    public final Short shorts;
    public final Boolean booleans;
    public final Long longs;

    public SimplePojo(final Integer integer, final Character character, final String string, final Byte bytes,
                      final Short shorts, final Boolean booleans, final Long longs) {
        this.integer = integer;
        this.character = character;
        this.string = string;
        this.bytes = bytes;
        this.shorts = shorts;
        this.booleans = booleans;
        this.longs = longs;
    }
}
