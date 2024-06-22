package unittests;

import model.Pojo;
import model.PojoWithAttribute;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class EscapePojoTest {

    private final XmlParser parserDefault = new XmlParser();
    private final XmlParser parserEncodeUTF8 = newXmlParser().shouldEncodeUTF8().build();

    @Test
    public void serializePojoWithDangerousCharsInText() {
        final var inputPojo = new Pojo("<>& and something \" ' '");
        final var expected = "<pojo>\n  <name>&lt;&gt;&amp; and something &quot; &apos; &apos;</name>\n</pojo>\n";

        final var actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializePojoWithDangerousCharsInAttribute() {
        final var inputPojo = new PojoWithAttribute("<>& and something \" ' '");
        final var expected = "<pojowithattribute name=\"&lt;&gt;&amp; and something &quot; &apos; &apos;\" />\n";

        final var actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializePojoWithEscapedCharsInText() {
        final var inputXml = "<pojo>\n  <name>&lt;&gt;&amp; and something &quot; &apos; &apos;</name>\n</pojo>\n";
        final var expected = new Pojo("<>& and something \" ' '");

        final var actual = parserDefault.fromXml(inputXml, Pojo.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.name, actual.name);
    }

    @Test
    public void deserializePojoWithEscapedCharsInAttribute() {
        final var inputXml = "<pojowithattribute name=\"&lt;&gt;&amp; and something &quot; &apos; &apos;\" />\n";
        final var expected = new PojoWithAttribute("<>& and something \" ' '");

        final var actual = parserDefault.fromXml(inputXml, PojoWithAttribute.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.name, actual.name);
    }

    @Test
    public void serializePojoUTF8Characters() {
        final var inputPojo = new Pojo("ƑƟƠƄǠȒ");
        final var expected = "<pojo>\n  <name>&#401;&#415;&#416;&#388;&#480;&#530;</name>\n</pojo>\n";

        final var actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializePojoUTF8Characters() {
        final var inputPojo = new Pojo("ƑƟƠƄǠȒ");
        final var expected = "<pojo>\n  <name>ƑƟƠƄǠȒ</name>\n</pojo>\n";

        final var actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializePojoUTF8Characters() {
        final var inputXml = "<pojo>\n  <name>&#401;&#415;&#416;&#388;&#480;&#530;</name>\n</pojo>\n";
        final var expected = new Pojo("ƑƟƠƄǠȒ");

        final var actual = parserEncodeUTF8.fromXml(inputXml, Pojo.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.name, actual.name);
    }

}
