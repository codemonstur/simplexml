package example;

import simplexml.SimpleXml;
import simplexml.model.Element;

import static simplexml.model.Element.element;

public final class Builder {

    public static void main(final String... args) {
        final Element node =
            element("element")
                .attribute("name", "value")
                .child(element("child"))
                .text("Some text");

        final SimpleXml simple = new SimpleXml();
        System.out.println(simple.domToXml(node));
    }

}
