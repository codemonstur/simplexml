package unittests;

import model.MissingPathPojo;
import model.MissingPathRecord;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.*;

public class MissingPathTest {

    private static final String POJO_XML = """
            <missingpathpojo>
              <wrapper>
                <found>Hello</found>
              </wrapper>
            </missingpathpojo>
            """;

    private static final String RECORD_XML = """
            <missingpathrecord>
              <wrapper>
                <found>Hello</found>
              </wrapper>
            </missingpathrecord>
            """;

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeClass() {
        final var actual = parser.fromXml(POJO_XML, MissingPathPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.found, "Pojo has the wrong value");
        assertNull(actual.value, "Value should be null when its path does not resolve");
        assertNull(actual.values, "Values should be null when its path does not resolve");
        assertNull(actual.wrappedValues, "Wrapped values should be null when its path does not resolve");
    }

    @Test
    public void deserializeClassWithoutWrapper() {
        final var actual = parser.fromXml("<missingpathpojo></missingpathpojo>", MissingPathPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertNull(actual.value, "Value should be null when its path does not resolve");
        assertNull(actual.values, "Values should be null when its path does not resolve");
        assertNull(actual.wrappedValues, "Wrapped values should be null when its path does not resolve");
    }

    @Test
    public void deserializeRecord() {
        final var actual = parser.fromXml(RECORD_XML, MissingPathRecord.class);

        assertNotNull(actual, "Record is null");
        assertEquals("Hello", actual.found(), "Record has the wrong value");
        assertNull(actual.value(), "Value should be null when its path does not resolve");
        assertNull(actual.values(), "Values should be null when its path does not resolve");
        assertNull(actual.wrappedValues(), "Wrapped values should be null when its path does not resolve");
    }

    @Test
    public void deserializeRecordWithoutWrapper() {
        final var actual = parser.fromXml("<missingpathrecord></missingpathrecord>", MissingPathRecord.class);

        assertNotNull(actual, "Record is null");
        assertNull(actual.value(), "Value should be null when its path does not resolve");
        assertNull(actual.values(), "Values should be null when its path does not resolve");
        assertNull(actual.wrappedValues(), "Wrapped values should be null when its path does not resolve");
    }

}
