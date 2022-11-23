package model;

import xmlparser.annotations.XmlEnumValue;

public enum SimpleEnum {
    one, two, three,
    @XmlEnumValue("123")
    value
}