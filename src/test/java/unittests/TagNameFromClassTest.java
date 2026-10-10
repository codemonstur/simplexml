package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlName;
import xmlparser.annotations.XmlNameFromClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TagNameFromClassTest {

    private static class Envelope {
        @XmlNameFromClass
        public final Object body;

        private Envelope(final Object body) {
            this.body = body;
        }
    }
    @XmlName("get")
    private static class GetAction {
        public final String id;

        private GetAction(final String id) {
            this.id = id;
        }
    }
    @XmlName("email")
    private static class EmailAction {
        public final String address;

        private EmailAction(final String address) {
            this.address = address;
        }
    }

    private final XmlParser parser = new XmlParser();

    private static final String XML_GET_ACTION = """
            <envelope>
              <get>
                <id>1</id>
              </get>
            </envelope>
            """;

    @Test
    public void serializeEnvelopeWithGetAction() {
        final var input = new Envelope(new GetAction("1"));

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", XML_GET_ACTION, actual);
    }

    private static final String XML_EMAIL_ACTION = """
            <envelope>
              <email>
                <address>test@acme.org</address>
              </email>
            </envelope>
            """;

    @Test
    public void serializeEnvelopeWithEmailAction() {
        final var input = new Envelope(new EmailAction("test@acme.org"));

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", XML_EMAIL_ACTION, actual);
    }

    private static final String XML_OBJECT_ACTION = """
            <envelope>
              <object />
            </envelope>
            """;

    @Test
    public void serializeEnvelopeWithObject() {
        final var input = new Envelope(new Object());

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", XML_OBJECT_ACTION, actual);
    }

}
