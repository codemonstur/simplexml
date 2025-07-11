package model;

import xmlparser.annotations.XmlDefaultValue;

public record SimpleWithDefaultsRecord(
    @XmlDefaultValue("1") Integer integer,
    @XmlDefaultValue("a") Character character,
    @XmlDefaultValue("hello") String string,
    @XmlDefaultValue("10") Byte bytes,
    @XmlDefaultValue("1") Short shorts,
    @XmlDefaultValue("true") Boolean booleans,
    @XmlDefaultValue("1") Long longs)
{}
