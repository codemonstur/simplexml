package unittests;

import model.Pojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class EncodingTest {

    private final XmlParser parserDefault = new XmlParser();
    private final XmlParser parserEncodeUTF8 = newXmlParser().shouldEncodeUTF8().build();

    @Test
    public void serializeWithDangerousChars() {
        final String pojoXml = "<pojo>\n  <name>&lt;&gt;&amp; and something &quot; &apos; &apos;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("<>& and something \" ' '");

        final String xml = parserDefault.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeWithEscapedChars() {
        final String pojoXml = "<pojo>\n  <name>&lt;&gt;&amp; and something &quot; &apos; &apos;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("<>& and something \" ' '");

        final Pojo xml = parserDefault.fromXml(pojoXml, Pojo.class);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojo.name, xml.name);
    }

    @Test
    public void serializeUTF8Characters() {
        final String pojoXml = "<pojo>\n  <name>&#401;&#415;&#416;&#388;&#480;&#530;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("ƑƟƠƄǠȒ");

        final String xml = parserEncodeUTF8.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void dontSerializeUTF8Characters() {
        final String pojoXml = "<pojo>\n  <name>ƑƟƠƄǠȒ</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("ƑƟƠƄǠȒ");

        final String xml = parserDefault.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeUTF8Characters() {
        final String pojoXml = "<pojo>\n  <name>&#401;&#415;&#416;&#388;&#480;&#530;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("ƑƟƠƄǠȒ");

        final Pojo xml = parserEncodeUTF8.fromXml(pojoXml, Pojo.class);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojo.name, xml.name);
    }

}
