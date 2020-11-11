package question;

import xmlparser.XmlParser;
import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlFieldDeserializer;
import xmlparser.model.XmlElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Example taken from:
 * https://stackoverflow.com/questions/57910705/how-to-parse-a-complex-element-into-a-map-using-a-attribute-as-key-and-the-whol/57923435#57923435
 */
public class Question3 {

    private static final String xml =
        "<document code=\"100\" clazz=\"DocumentA\">\n" +
        "    <properties>\n" +
        "        <property name=\"PropA\" value=\"123\" />\n" +
        "        <property name=\"PropB\" value=\"qwerty\" />\n" +
        "        <property name=\"PropC\" value=\"ABC\" />\n" +
        "    </properties>\n" +
        "</document>";

    private static class Document {
        @XmlAttribute
        private Integer code;
        @XmlAttribute
        private String clazz;
        @XmlFieldDeserializer(clazz="question.Question3", function="deserializeProperties")
        private Map<String, Property> properties;

        // This also works
//        @XmlWrapperTag("properties")
//        @XmlName("property")
//        private List<Property> properties;
    }
    private static class Property {
        @XmlAttribute
        private String name;
        @XmlAttribute
        private String value;

        private Property(final String name, final String value) {
            this.name = name;
            this.value = value;
        }
    }

    public static void main(final String... args) {
        final XmlParser parser = new XmlParser();
        final Document document = parser.fromXml(xml, Document.class);
        System.out.println(document.code + " : " + document.clazz + " : " + document.properties);
    }

    public static Map<String, Property> deserializeProperties(final XmlElement parent) {
        final Map<String, Property> map = new HashMap<>();
        for (final XmlElement property : parent.findChildForName("properties", null).children) {
            map.put(property.attributes.get("name"), new Property(property.attributes.get("name"), property.attributes.get("value")));
        }
        return map;
    }
}
