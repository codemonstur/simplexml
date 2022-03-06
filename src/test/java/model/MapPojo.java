package model;

import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlMapWithChildNodes;
import xmlparser.annotations.XmlName;

import java.util.Map;

public final class MapPojo {

    public final Map<Integer, String> map1;
    @XmlMapWithAttributes(keyName = "key", valueName = "value")
    public final Map<Integer, String> map2;
    @XmlMapWithChildNodes(keyName = "key", valueName = "value")
    public final Map<Integer, String> map3;
    @XmlMapWithAttributes(keyName = "key")
    public final Map<String, String> map4;
    @XmlMapWithChildNodes(keyName = "key")
    public final Map<Integer, String> map5;
    @XmlName("item")
    @XmlMapWithAttributes(keyName = "key", valueName = "value")
    public final Map<Integer, String> map6;

    public MapPojo(final Map<Integer, String> map1, final Map<Integer, String> map2, final Map<Integer, String> map3,
                   final Map<String, String> map4, final Map<Integer, String> map5, final Map<Integer, String> map6) {
        this.map1 = map1;
        this.map2 = map2;
        this.map3 = map3;
        this.map4 = map4;
        this.map5 = map5;
        this.map6 = map6;
    }
}
