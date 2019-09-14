package example;

import xmlparser.XmlParser;
import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlName;
import xmlparser.annotations.XmlNoExport;
import xmlparser.annotations.XmlTextNode;

public final class Serialize {

    private static class Project {
        @XmlAttribute
        @XmlName("other")
        public final String name;

        @XmlTextNode
        public final String text;

        @XmlNoExport
        public final String hidden;

        private Project(final String name, final String text, final String hidden) {
            this.name = name;
            this.text = text;
            this.hidden = hidden;
        }
    }

    public static void main(final String... args) {
        final Project project = new Project("test<>&\"'", "Just a project", "invisible");

        final XmlParser parser = new XmlParser();
        System.out.println(parser.toXml(project));
    }

}
