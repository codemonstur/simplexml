package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlTextNode;
import xmlparser.error.InvalidCData;

import static org.junit.Assert.*;

public class CdataTest {

    private static class TextPojo {
        @XmlTextNode(cdata = true)
        public final String text;

        private TextPojo(final String text) {
            this.text = text;
        }
    }

    private final XmlParser parser = new XmlParser();

    private static final String XML_CDATA_TEST = "<textpojo><![CDATA[test]]></textpojo>\n";

    @Test
    public void serializesCData() {
        final var output = parser.toXml(new TextPojo("test"));

        assertNotNull("No serialization response", output);
        assertEquals("Serialized XML does not match", XML_CDATA_TEST, output);
    }

    private static final String XML_CDATA_WITH_TAGS = "<textpojo><![CDATA[test<some>content</some>]]></textpojo>\n";

    @Test
    public void serializesCDataWithTags() {
        final var output = parser.toXml(new TextPojo("test<some>content</some>"));

        assertNotNull("No serialization response", output);
        assertEquals("Serialized XML does not match", XML_CDATA_WITH_TAGS, output);
    }

    @Test(expected = InvalidCData.class)
    public void throwsErrorOnInvalidInput() {
        parser.toXml(new TextPojo("]]>"));

        fail("Test did not throw an exception");
    }

}
