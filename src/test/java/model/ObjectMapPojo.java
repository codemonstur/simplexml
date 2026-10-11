package model;

import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlMapWithChildNodes;
import xmlparser.annotations.XmlName;

import java.util.Map;

/**
 * Maps whose keys and values are objects, instead of the natives that have a deserializer.
 */
public final class ObjectMapPojo {

    @XmlMapTagIsKey
    public final Map<String, Pojo> tagIsKeyValue;
    @XmlMapTagIsKey
    public final Map<Pojo, String> tagIsKeyKey;
    @XmlMapWithChildNodes(keyName = "key", valueName = "value")
    public final Map<Pojo, Pojo> childNodeEntries;
    @XmlMapWithAttributes(keyName = "key", valueName = "value")
    public final Map<String, Pojo> attributeValues;
    @XmlMapTagIsKey
    public final Map<SimpleEnum, String> enumKeys;
    @XmlMapWithAttributes(keyName = "key", valueName = "value")
    public final Map<String, SimpleEnum> enumValues;

    public ObjectMapPojo(final Map<String, Pojo> tagIsKeyValue, final Map<Pojo, String> tagIsKeyKey,
                         final Map<Pojo, Pojo> childNodeEntries, final Map<String, Pojo> attributeValues,
                         final Map<SimpleEnum, String> enumKeys, final Map<String, SimpleEnum> enumValues) {
        this.tagIsKeyValue = tagIsKeyValue;
        this.tagIsKeyKey = tagIsKeyKey;
        this.childNodeEntries = childNodeEntries;
        this.attributeValues = attributeValues;
        this.enumKeys = enumKeys;
        this.enumValues = enumValues;
    }
}
