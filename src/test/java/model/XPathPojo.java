package model;

import xmlparser.annotations.XmlPath;
import xmlparser.annotations.XmlTextNode;

public final class XPathPojo {

    @XmlTextNode
    @XmlPath("one/two/three")
    public final String value;

    public XPathPojo(final String value) {
        this.value = value;
    }

}
