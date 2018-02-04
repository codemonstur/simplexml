package tools;

import simplexml.SimpleXml;
import simplexml.model.ElementNode;

import static simplexml.model.ElementNode.element;

public class Builder {

    public static void main(final String... args) {
        final ElementNode node =
            element("name")
                .attribute("name", "value")
                .child(element("child"))
                .text("Some text");

        final SimpleXml simple = new SimpleXml();
        System.out.println(simple.domToXml(node));
    }

}
