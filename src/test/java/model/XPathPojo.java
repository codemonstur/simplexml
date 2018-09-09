package model;

import simplexml.annotations.XmlPath;
import simplexml.annotations.XmlTextNode;

public final class XPathPojo {

    @XmlTextNode
    @XmlPath("one/two/three")
    public final String value;

    public XPathPojo(final String value) {
        this.value = value;
    }

}
