package question;

import xmlparser.XmlParser;
import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlName;

import java.util.HashMap;
import java.util.Map;

/**
 * https://stackoverflow.com/questions/64332094/xstream-namedmapconverter-and-hashmap-without-container-node
 */
public final class Question4 {

    @XmlName("object")
    public static class MyObject {
        @XmlAttribute
        private String name = "test";
        @XmlMapWithAttributes(keyName = "language")
        private Map<String, String> description = new HashMap<>();

        private MyObject() {
            description.put("en", "Test en");
            description.put("fr", "Test fr");
        }
    }

    public static void main(final String... args) {
        XmlParser parser = new XmlParser();
        System.out.println(parser.toXml(new MyObject()));
    }

}
