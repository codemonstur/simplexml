package unittests;

import model.RecordPojo;
import model.RecordWithPrimitives;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void serializeRecord() {
        final var input = new RecordPojo("Hello");
        final var expected = "<recordpojo>\n  <name>Hello</name>\n</recordpojo>\n";

        final var actual = parser.toXml(input);

        assertNotNull(actual, "No serialization response");
        assertEquals(expected, actual, "Invalid serialized output");
    }

    @Test
    public void deserializeRecord() {
        final var input = "<pojo>\n  <name>Hello</name>\n</pojo>\n";

        final var actual = parser.fromXml(input, RecordPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name(), "Pojo has the wrong name");
    }

    @Test
    public void deserializeMissingPrimitives() {
        final var actual = parser.fromXml("<recordwithprimitives/>", RecordWithPrimitives.class);

        assertNotNull(actual, "Record is null");
        assertEquals(0, actual.id(), "Int should have its zero value");
        assertFalse(actual.active(), "Boolean should have its zero value");
        assertEquals(0L, actual.skipped(), "NoImport component should have its zero value");
        assertEquals(0d, actual.measured(), 0d, "Missing path component should have its zero value");
        assertNull(actual.name(), "String component should be null");
    }

    @Test
    public void deserializePresentPrimitivesWithIgnoredComponents() {
        final var input = "<recordwithprimitives><id>5</id><skipped>42</skipped>"
                + "<measured>2.5</measured><name>Hello</name></recordwithprimitives>";

        final var actual = parser.fromXml(input, RecordWithPrimitives.class);

        assertNotNull(actual, "Record is null");
        assertEquals(5, actual.id(), "Pojo has the wrong id");
        assertFalse(actual.active(), "Boolean should have its zero value");
        assertEquals(0L, actual.skipped(), "NoImport component should not have been read");
        assertEquals(0d, actual.measured(), 0d, "Missing path component should not have been read");
        assertEquals("Hello", actual.name(), "Pojo has the wrong name");
    }

}
