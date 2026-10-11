package model;

import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlDefaultValue;
import xmlparser.annotations.XmlNameFromClass;
import xmlparser.annotations.XmlTextNode;
import xmlparser.annotations.XmlWrapperTag;

public final class NullDefaultsPojo {

    @XmlAttribute
    @XmlDefaultValue("5")
    public Integer attribute;
    @XmlWrapperTag("wrap")
    @XmlDefaultValue("hello")
    public String wrapped;
    @XmlNameFromClass
    @XmlDefaultValue("body")
    public Object body;
    @XmlDefaultValue("tail")
    public String tail;
    @XmlTextNode
    @XmlDefaultValue("text")
    public String text;

    public NullDefaultsPojo() {
    }

    public NullDefaultsPojo(final Integer attribute, final String wrapped, final Object body, final String tail,
                            final String text) {
        this.attribute = attribute;
        this.wrapped = wrapped;
        this.body = body;
        this.tail = tail;
        this.text = text;
    }

}
