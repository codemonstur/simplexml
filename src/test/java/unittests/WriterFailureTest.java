package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.XmlWriter;
import xmlparser.error.InvalidXml;
import xmlparser.parsing.ObjectSerializer;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class WriterFailureTest {

    private static final String CLASS = "model.Pojo";
    private static final String MESSAGE = "Could not read the fields of " + CLASS;

    /** A writer whose serializer lookup fails, standing in for any failure part way through a write. */
    private static final XmlWriter FAILING = new XmlWriter() {
        public String newLine() { return "\n"; }
        public String escape(final String input) { return input; }
        public boolean shouldPrettyPrint() { return true; }
        public boolean hasSerializer(final Class<?> type) { return false; }
        public ObjectSerializer getSerializer(final Class<?> type) {
            throw new IllegalArgumentException("no serializer for " + type);
        }
    };

    private final XmlParser parser = new XmlParser();

    @Test
    public void aFailureWhileWritingRaisesAnErrorInsteadOfReturningTruncatedXml() {
        try {
            final String actual = FAILING.toXml(new Pojo("hi"));

            fail("XML that was never written was returned: " + actual);
        } catch (final InvalidXml e) {
            assertEquals("Invalid error message", MESSAGE, e.getMessage());
        }
    }

    @Test
    public void bothToXmlOverloadsFailTheSameWay() throws Exception {
        try {
            FAILING.toXml(new Pojo("hi"));
            fail("XML that was never written was returned");
        } catch (final InvalidXml fromTheStringOverload) {
            final var writer = new StringWriter();
            try {
                FAILING.toXml(new Pojo("hi"), writer);

                fail("XML that was never written was written");
            } catch (final InvalidXml fromTheWriterOverload) {
                assertEquals("The overloads report a failure differently",
                        fromTheStringOverload.getMessage(), fromTheWriterOverload.getMessage());
            }
        }
    }

    @Test
    public void theCauseOfAFailureWhileWritingIsKept() {
        try {
            FAILING.toXml(new Pojo("hi"));

            fail("XML that was never written was returned");
        } catch (final InvalidXml e) {
            assertNotNull("The cause of the failure was dropped", e.getCause());
            assertEquals("Invalid cause", IllegalArgumentException.class, e.getCause().getClass());
        }
    }

    @Test
    public void writingWithoutAFailureIsUnaffected() {
        assertEquals("Writing changed", "<pojo>\n  <name>hello</name>\n</pojo>\n", parser.toXml(new Pojo("hello")));
    }

    @Test
    public void theCauseOfAFailureWhileReadingIsKept() {
        final var withCause = new InvalidXml("message", new IOException("because"));

        assertEquals("The message was not kept", "message", withCause.getMessage());
        assertNotNull("The cause of the failure was dropped", withCause.getCause());
    }

}
