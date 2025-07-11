package model;

import xmlparser.annotations.XmlDefaultValue;

public final class PojoWithDefaults {

    @XmlDefaultValue("hello")
    public String field1;

    @XmlDefaultValue(value = "hello", deserialize = false)
    public String field2;

    @XmlDefaultValue(value = "hello", serialize = false)
    public String field3;

}
