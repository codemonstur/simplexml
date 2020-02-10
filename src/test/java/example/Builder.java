package example;

import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static xmlparser.model.XmlElement.newElement;

public final class Builder {

    public static void main(final String... args) {
        final XmlElement node =
            newElement("element")
                .attribute("name", "value")
                .child(newElement("child"))
                .text("Some text")
                .build();

        final XmlParser parser = new XmlParser();
        System.out.println(parser.domToXml(node));
    }

}
