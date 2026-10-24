package unittests;

import model.RecordPojoWithAnnotation;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RecordWithAnnotationTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void serializeRecord() {
        final var input = new RecordPojoWithAnnotation("Hello");
        final var expected = "<recordpojo>\n  <other>Hello</other>\n</recordpojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void deserializeRecord() {
        final var input = "<recordpojo>\n  <other>Hello</other>\n</recordpojo>\n";

        final var actual = parser.fromXml(input, RecordPojoWithAnnotation.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name(), "Pojo has the wrong name");
    }

}
