package unittests;

import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;

public class CompressTest {

    private static final String POM_WITH_WHITESPACE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<  project xmlns=\"http://maven.apache.org/POM/4.0.0\"   \t\n   " +
        "    xmlns:xsi   =    \"http://www.w3.org/2001/XMLSchema-instance\"" +
        "    xsi:schemaLocation   =\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
        "<groupId>       com.github.codemonstur</groupId>" +
        "     <artifactId>simplexml       </artifactId>     " +
        "<version>1.0.0</version>\n\n\n\n" +
        "\t\t</project>\n\n\n";

    private static final String POM_COMPRESSED = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"><groupId>com.github.codemonstur</groupId><artifactId>simplexml</artifactId><version>1.0.0</version></project>";

    @Test
    public void compress() {
        final XmlParser parser = new XmlParser();
        final String compressed = parser.compressXml(POM_WITH_WHITESPACE);
        assertEquals("POMs was not adequately compressed", POM_COMPRESSED, compressed);
    }

}
