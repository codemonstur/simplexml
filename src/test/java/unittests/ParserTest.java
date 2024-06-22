package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void writeToWriter() throws IOException {
        final var input = new Pojo("hello");
        final var expected = "<pojo>\n  <name>hello</name>\n</pojo>\n";

        try (final var writer = new StringWriter()) {
            parser.toXml(input, writer);

            assertEquals(expected, writer.toString());
        }
    }

}
