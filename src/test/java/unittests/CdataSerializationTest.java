package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlTextNode;
import xmlparser.error.InvalidCData;

import static org.junit.jupiter.api.Assertions.*;

public class CdataSerializationTest {

    private static class TextPojo {
        @XmlTextNode(cdata = true)
        public final String text;

        private TextPojo(final String text) {
            this.text = text;
        }
    }

    private final XmlParser parser = new XmlParser();

    @Test
    public void serializesCData() {
        final var output = parser.toXml(new TextPojo("test"));

        assertNotNull(output, "No serialization response");
        assertEquals("<textpojo><![CDATA[test]]></textpojo>\n", output, "Serialized XML does not match");
    }

    @Test
    public void serializesCDataWithTags() {
        final var output = parser.toXml(new TextPojo("test<some>content</some>"));

        assertNotNull(output, "No serialization response");
        assertEquals("<textpojo><![CDATA[test<some>content</some>]]></textpojo>\n", output, "Serialized XML does not match");
    }

    @Test
    public void throwsErrorOnInvalidInput() {
        assertThrows(InvalidCData.class, () -> parser.toXml(new TextPojo("]]>")), "Test did not throw an exception");
    }
}