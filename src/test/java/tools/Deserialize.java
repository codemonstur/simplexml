package tools;

import simplexml.SimpleXml;

import java.io.IOException;

public final class Deserialize {

    private static final String POM_1 =
        "<project>" +
            "<groupId>codemonster</groupId>" +
            "<artifactId>simplexml</artifactId>" +
            "<version>1.0.0</version>" +
        "</project>";

    private static final String POM_2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
        "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
        "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
            "<groupId>codemonster</groupId>" +
            "<artifactId>simplexml</artifactId>" +
            "<version>1.0.0</version>" +
        "</project>";

    private static class Project {
        public final String groupId;
        public final String artifactId;
        public final String version;

        private Project(final String groupId, final String artifactId, final String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }
    }

    public static void main(final String... args) throws IOException {
        final SimpleXml simple = new SimpleXml();

        final Project project1 = simple.fromXml(POM_1, Project.class);
        System.out.println(project1.groupId + ":" + project1.artifactId + ":" + project1.version);

        final Project project2 = simple.fromXml(POM_2, Project.class);
        System.out.println(project2.groupId + ":" + project2.artifactId + ":" + project2.version);
    }

}
