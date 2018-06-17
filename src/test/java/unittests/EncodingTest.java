package unittests;

import model.Pojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static simplexml.SimpleXml.newSimpleXml;

public class EncodingTest {

    private SimpleXml simpleDefault = new SimpleXml();
    private SimpleXml simpleEncodeUTF8 = newSimpleXml().shouldEncodeUTF8().build();

    @Test
    public void serializeWithDangerousChars() {
        final String pojoXml = "<pojo>\n  <name>&lt;&gt;&amp; and something &quot; &apos; &apos;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("<>& and something \" ' '");

        final String xml = simpleDefault.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeWithEscapedChars() throws IOException {
        final String pojoXml = "<pojo>\n  <name>&lt;&gt;&amp; and something &quot; &apos; &apos;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("<>& and something \" ' '");

        final Pojo xml = simpleDefault.fromXml(pojoXml, Pojo.class);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojo.name, xml.name);

    }

    @Test
    public void serializeUTF8Characters() {
        final String pojoXml = "<pojo>\n  <name>&#401;&#415;&#416;&#388;&#480;&#530;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("ƑƟƠƄǠȒ");

        final String xml = simpleEncodeUTF8.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void dontSerializeUTF8Characters() {
        final String pojoXml = "<pojo>\n  <name>ƑƟƠƄǠȒ</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("ƑƟƠƄǠȒ");

        final String xml = simpleDefault.toXml(pojo);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojoXml, xml);
    }

    @Test
    public void deserializeUTF8Characters() throws IOException {
        final String pojoXml = "<pojo>\n  <name>&#401;&#415;&#416;&#388;&#480;&#530;</name>\n</pojo>\n";
        final Pojo pojo = new Pojo("ƑƟƠƄǠȒ");

        final Pojo xml = simpleEncodeUTF8.fromXml(pojoXml, Pojo.class);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", pojo.name, xml.name);

    }

}
