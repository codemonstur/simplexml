package unittests;

import model.RecordPojo;
import model.RecordPojoWithAnnotation;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecordWithAnnotationTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void serializeRecord() {
        final var input = new RecordPojoWithAnnotation("Hello");
        final var expected = "<recordpojo>\n  <other>Hello</other>\n</recordpojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeRecord() {
        final var input = "<recordpojo>\n  <other>Hello</other>\n</recordpojo>\n";

        final var actual = parser.fromXml(input, RecordPojoWithAnnotation.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name());
    }

}
