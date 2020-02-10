package example;

import xmlparser.XmlParser;
import xmlparser.annotations.XmlAttribute;

import java.util.HashMap;
import java.util.Map;

import static xmlparser.XmlParser.newXmlParser;

public class PrettyPrint {

    public static class Pojo {
        public Map<String, Field> map = new HashMap<>();
    }
    public static class Field {
        @XmlAttribute
        public String attribute;
    }

    public static void main(final String... args) {
        // Pretty printing is actually the default
        final XmlParser parser = newXmlParser().shouldPrettyPrint().build();
        final Field field = new Field();
        field.attribute = "value";
        final Pojo pojo = new Pojo();
        pojo.map.put("key", field);
        System.out.println(parser.toXml(pojo));
    }

}
