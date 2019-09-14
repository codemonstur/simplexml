package example;

import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static xmlparser.model.XmlElement.element;

public final class Builder {

    public static void main(final String... args) {
        final XmlElement node =
            element("element")
                .attribute("name", "value")
                .child(element("child"))
                .text("Some text");

        final XmlParser parser = new XmlParser();
        System.out.println(parser.domToXml(node));
    }

}
