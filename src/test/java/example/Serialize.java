package example;

import simplexml.SimpleXml;
import simplexml.annotations.XmlAttribute;
import simplexml.annotations.XmlName;
import simplexml.annotations.XmlNoExport;
import simplexml.annotations.XmlTextNode;

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

        final SimpleXml simple = new SimpleXml();
        System.out.println(simple.toXml(project));
    }

}
