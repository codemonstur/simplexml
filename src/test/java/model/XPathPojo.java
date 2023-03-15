package model;

import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlPath;
import xmlparser.annotations.XmlTextNode;

public final class XPathPojo {

    @XmlPath("wrapper")
    public final String object;

    @XmlAttribute
    @XmlPath("wrapper")
    public final String attr;

    @XmlTextNode
    @XmlPath("one/two/three")
    public final String value;

    public XPathPojo(final String object, final String value, final String attr) {
        this.object = object;
        this.value = value;
        this.attr = attr;
    }

}
