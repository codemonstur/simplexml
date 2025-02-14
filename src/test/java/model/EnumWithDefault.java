package model;

import xmlparser.annotations.XmlEnumDefaultValue;

public enum EnumWithDefault {
    one, two, @XmlEnumDefaultValue three
}
