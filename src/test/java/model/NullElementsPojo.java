package model;

import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlName;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NullElementsPojo {

    @XmlName("list")
    public List<String> list;
    @XmlName("set")
    public Set<String> set;
    @XmlName("array")
    public String[] array;
    @XmlName("map")
    public Map<String, String> map;
    @XmlName("map2")
    @XmlMapWithAttributes(keyName = "key")
    public Map<String, String> mapWithAttributes;

    public NullElementsPojo() {
    }

    public NullElementsPojo(final List<String> list, final Set<String> set, final String[] array,
                            final Map<String, String> map, final Map<String, String> mapWithAttributes) {
        this.list = list;
        this.set = set;
        this.array = array;
        this.map = map;
        this.mapWithAttributes = mapWithAttributes;
    }

}
