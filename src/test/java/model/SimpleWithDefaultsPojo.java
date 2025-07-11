package model;

import xmlparser.annotations.XmlDefaultValue;

public final class SimpleWithDefaultsPojo {

    @XmlDefaultValue("1")
    public Integer integer;
    @XmlDefaultValue("a")
    public Character character;
    @XmlDefaultValue("hello")
    public String string;
    @XmlDefaultValue("10")
    public Byte bytes;
    @XmlDefaultValue("1")
    public Short shorts;
    @XmlDefaultValue("true")
    public Boolean booleans;
    @XmlDefaultValue("1")
    public Long longs;

}
