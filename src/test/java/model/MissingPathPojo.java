package model;

import xmlparser.annotations.XmlPath;
import xmlparser.annotations.XmlWrapperTag;

import java.util.List;

public final class MissingPathPojo {

    @XmlPath("wrapper/missing")
    public String value;

    @XmlPath("wrapper")
    public String found;

    @XmlPath("wrapper/missing")
    public List<String> values;

    @XmlWrapperTag("wrap")
    @XmlPath("wrapper/missing")
    public List<String> wrappedValues;

}
