package model;

import xmlparser.annotations.XmlAttribute;

public final class PojoWithAttribute {

    @XmlAttribute
    public final String name;

    public PojoWithAttribute(final String name) {
        this.name = name;
    }

}
