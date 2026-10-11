package unittests;

import model.MissingPathPojo;
import model.MissingPathRecord;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong value", "Hello", actual.found);
        assertNull("Value should be null when its path does not resolve", actual.value);
        assertNull("Values should be null when its path does not resolve", actual.values);
        assertNull("Wrapped values should be null when its path does not resolve", actual.wrappedValues);
    }

    @Test
    public void deserializeClassWithoutWrapper() {
        final var actual = parser.fromXml("<missingpathpojo></missingpathpojo>", MissingPathPojo.class);

        assertNotNull("Pojo is null", actual);
        assertNull("Value should be null when its path does not resolve", actual.value);
        assertNull("Values should be null when its path does not resolve", actual.values);
        assertNull("Wrapped values should be null when its path does not resolve", actual.wrappedValues);
    }

    @Test
    public void deserializeRecord() {
        final var actual = parser.fromXml(RECORD_XML, MissingPathRecord.class);

        assertNotNull("Record is null", actual);
        assertEquals("Record has the wrong value", "Hello", actual.found());
        assertNull("Value should be null when its path does not resolve", actual.value());
        assertNull("Values should be null when its path does not resolve", actual.values());
        assertNull("Wrapped values should be null when its path does not resolve", actual.wrappedValues());
    }

    @Test
    public void deserializeRecordWithoutWrapper() {
        final var actual = parser.fromXml("<missingpathrecord></missingpathrecord>", MissingPathRecord.class);

        assertNotNull("Record is null", actual);
        assertNull("Value should be null when its path does not resolve", actual.value());
        assertNull("Values should be null when its path does not resolve", actual.values());
        assertNull("Wrapped values should be null when its path does not resolve", actual.wrappedValues());
    }

}
