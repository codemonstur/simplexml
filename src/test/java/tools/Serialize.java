package tools;

import simplexml.SimpleXml;
import simplexml.model.XmlAttribute;
import simplexml.model.XmlName;
import simplexml.model.XmlNoExport;
import simplexml.model.XmlTextNode;

public class Serialize {

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

        final SimpleXml simple = new SimpleXml();
        System.out.println(simple.toXml(project));
    }

}
