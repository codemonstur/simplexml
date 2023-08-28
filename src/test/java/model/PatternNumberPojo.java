package model;

import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlTextNode;

public class PatternNumberPojo {

    @XmlAttribute(pattern = "[0-9]{4}")
    public Integer field;

    @XmlTextNode(pattern = "[0-9]{4}")
    public int text;

}
