package model;

import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlTextNode;

public class PojoSelfClosed {

    @XmlAttribute
    public final String name;
    @XmlTextNode
    public final String text;

    public PojoSelfClosed(final String name, final String text) {
        this.name = name;
        this.text = text;
    }

}
