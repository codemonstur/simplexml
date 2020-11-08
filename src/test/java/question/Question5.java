package question;

import xmlparser.XmlParser;
import xmlparser.annotations.XmlName;
import xmlparser.annotations.XmlTextNode;
import xmlparser.model.XmlElement;

import java.util.ArrayList;
import java.util.List;

// https://stackoverflow.com/questions/64710985/convert-single-xml-node-into-java-class
public final class Question5 {

    private static final String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
        "<!DOCTYPE note SYSTEM \"Note.dtd\">\n" +
        "<note>\n" +
            "<to>Tove</to>\n" +
            "<from>Jani</from>\n" +
            "<heading>Reminder</heading>\n" +
            "<body>Don't forget me this weekend!</body>\n" +
            "<sometag>\n" +
                "<table>\n" +
                    "<tr><td>content</td></tr>\n" +
                "</table>\n" +
            "</sometag>\n" +
        "</note>\n";

    @XmlName("table")
    private static class Table {
        private List<Question5.Row> tr;
    }
    private static class Row {
        private List<Cell> td;
    }
    private static class Cell {
        @XmlTextNode
        private String text;
    }

    public static void main(final String... args) {
        final XmlParser simple = new XmlParser();
        final XmlElement xmlElement = simple.fromXml(xml);
        final List<XmlElement> list = getElementsByTagName(xmlElement, "table");
        for (final XmlElement element : list) {
            Table table = simple.fromXml(element, Table.class);
            System.out.println(table.tr.get(0).td.get(0).text);
        }
    }

    private static List<XmlElement> getElementsByTagName(final XmlElement element, final String name) {
        final List<XmlElement> list = new ArrayList<>();
        getElementsByTagName(element, name, list);
        return list;
    }
    private static void getElementsByTagName(final XmlElement element, final String name, final List<XmlElement> list) {
        if (element == null) return;
        if (name.equals(element.name)) list.add(element);
        if (element.children == null) return;
        for (final XmlElement child : element.children) {
            getElementsByTagName(child, name, list);
        }
    }

    public static void main2(final String... args) {
        final XmlParser simple = new XmlParser();
        final XmlElement document = simple.fromXml(xml);
        for (final XmlElement element : document.getElementsByTagName("table")) {
            Table table = simple.fromXml(element, Table.class);
            System.out.println(table.tr.get(0).td.get(0).text);
        }
    }

}
