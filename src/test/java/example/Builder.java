package example;

import simplexml.SimpleXml;
import simplexml.model.XmlElement;

import static simplexml.model.XmlElement.element;

public final class Builder {

    public static void main(final String... args) {
        final XmlElement node =
            element("element")
                .attribute("name", "value")
                .child(element("child"))
                .text("Some text");

        final SimpleXml simple = new SimpleXml();
        System.out.println(simple.domToXml(node));
    }

}
