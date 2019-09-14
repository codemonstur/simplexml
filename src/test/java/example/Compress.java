package example;

import xmlparser.XmlParser;

public final class Compress {

    private static final String POM =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<  project xmlns=\"http://maven.apache.org/POM/4.0.0\"   \t\n   " +
        "    xmlns:xsi   =    \"http://www.w3.org/2001/XMLSchema-instance\"" +
        "    xsi:schemaLocation   =\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
        "<groupId>       com.github.codemonstur</groupId>" +
        "     <artifactId>simplexml       </artifactId>     " +
        "<version>1.0.0</version>\n\n\n\n" +
        "\t\t</project>\n\n\n";


    public static void main(final String... args) {
        final XmlParser parser = new XmlParser();
        System.out.println(parser.compressXml(POM));
    }

}
