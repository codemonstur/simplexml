package model;

import xmlparser.annotations.XmlTextNode;

public class PojoWithText {

    public String name;
    @XmlTextNode
    public String text;

    public PojoWithText(final String name, final String text) {
        this.name = name;
        this.text = text;
    }

}
