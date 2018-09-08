package model;

import simplexml.annotations.XmlWrapperTag;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WrappedPojo {

    @XmlWrapperTag("wrapper1")
    public final String string;
    @XmlWrapperTag("wrapper2")
    public final List<String> list;
    @XmlWrapperTag("wrapper3")
    public final Map<String, String> map;
    @XmlWrapperTag("wrapper4")
    public final Set<String> set;
    @XmlWrapperTag("wrapper5")
    public final String[] array;

    public WrappedPojo(final String string, final List<String> list, final Map<String, String> map,
                       final Set<String> set, final String[] array) {
        this.string = string;
        this.list = list;
        this.map = map;
        this.set = set;
        this.array = array;
    }

}
