package model;

import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlTextNode;

public class PatternPojo {

    @XmlAttribute(pattern = "[a-zA-Z]+")
    public String field;

    @XmlTextNode(pattern = "[a-zA-Z]+")
    public String text;

}
