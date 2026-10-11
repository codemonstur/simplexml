package unittests;

import model.RecordPojo;
import model.RecordWithPrimitives;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RecordTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void serializeRecord() {
        final var input = new RecordPojo("Hello");
        final var expected = "<recordpojo>\n  <name>Hello</name>\n</recordpojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeRecord() {
        final var input = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final var actual = parser.fromXml(input, RecordPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name());
    }

    @Test
    public void deserializeMissingPrimitives() {
        final var actual = parser.fromXml("<recordwithprimitives/>", RecordWithPrimitives.class);

        assertNotNull("Record is null", actual);
        assertEquals("Int should have its zero value", 0, actual.id());
        assertFalse("Boolean should have its zero value", actual.active());
        assertEquals("NoImport component should have its zero value", 0L, actual.skipped());
        assertEquals("Missing path component should have its zero value", 0d, actual.measured(), 0d);
        assertNull("String component should be null", actual.name());
    }

    @Test
    public void deserializePresentPrimitivesWithIgnoredComponents() {
        final var input = "<recordwithprimitives><id>5</id><skipped>42</skipped>"
                + "<measured>2.5</measured><name>Hello</name></recordwithprimitives>";

        final var actual = parser.fromXml(input, RecordWithPrimitives.class);

        assertNotNull("Record is null", actual);
        assertEquals("Pojo has the wrong id", 5, actual.id());
        assertFalse("Boolean should have its zero value", actual.active());
        assertEquals("NoImport component should not have been read", 0L, actual.skipped());
        assertEquals("Missing path component should not have been read", 0d, actual.measured(), 0d);
        assertEquals("Pojo has the wrong name", "Hello", actual.name());
    }

}
