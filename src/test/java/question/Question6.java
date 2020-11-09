package question;

import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static xmlparser.model.XmlElement.newElement;

// https://stackoverflow.com/questions/56551512/manually-constructing-node-tree-how-do-i-rename-objectnode-tags
public final class Question6 {

    public static void main(final String... args) {
        final XmlElement document = newElement("computer")
            .child(newElement("general")
                .child(newElement("name").text("computer-name-placeholder")))
            .build();
        XmlParser simple = new XmlParser();
        System.out.println(simple.domToXml(document));
    }

}
