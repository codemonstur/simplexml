package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlName;
import xmlparser.annotations.XmlNameFromClass;
import xmlparser.error.InvalidXml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @XmlName("a b")
    private static class InvalidNameAction {
        public final String id;

        private InvalidNameAction(final String id) {
            this.id = id;
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

        assertNotNull(actual, "No serialization response");
        assertEquals(XML_GET_ACTION, actual, "Invalid serialized output");
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

        assertNotNull(actual, "No serialization response");
        assertEquals(XML_EMAIL_ACTION, actual, "Invalid serialized output");
    }

    @Test
    public void serializeClassWithAnInvalidNameThrows() {
        final var e = assertThrows(InvalidXml.class, () -> parser.toXml(new InvalidNameAction("1")),
                "A class name that is not an XML name was written");

        assertEquals("Tag name 'a b' is not a valid XML name",
                e.getMessage(), "Invalid error message");
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

        assertNotNull(actual, "No serialization response");
        assertEquals(XML_OBJECT_ACTION, actual, "Invalid serialized output");
    }

}
