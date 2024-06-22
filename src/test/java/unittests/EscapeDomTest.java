package unittests;

import model.Pojo;
import model.PojoWithAttribute;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;
import static xmlparser.model.XmlElement.newElement;

public class EscapeDomTest {

    private final XmlParser parserDefault = new XmlParser();
    private final XmlParser parserEncodeUTF8 = newXmlParser().shouldEncodeUTF8().build();

    @Test
    public void serializeDomWithDangerousCharsInText() {
        final var inputDom = newElement("test").text("&<>'\"").build();
        final var expected = "<test>&amp;&lt;&gt;&apos;&quot;</test>\n";

        final var actual = parserDefault.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializeDomWithDangerousCharsInAttribute() {
        final var inputDom = newElement("test").attribute("hello", "&<>'\"").build();
        final var expected = "<test hello=\"&amp;&lt;&gt;&apos;&quot;\" />\n";

        final var actual = parserDefault.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeDomWithEscapedChars() {
        final var inputXml = "<pojo>&lt;&gt;&amp; and something &quot; &apos; &apos;</pojo>\n";
        final var expected = newElement("pojo").text("<>& and something \" ' '").build();

        final var actual = parserDefault.fromXml(inputXml);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid deserialized output", expected.name, actual.name);
        assertEquals("Invalid deserialized output", expected.getText(), actual.getText());
    }

    @Test
    public void serializeDomUTF8Characters() {
        final var inputDom = newElement("pojo").text("ƑƟƠƄǠȒ").build();
        final var expected = "<pojo>&#401;&#415;&#416;&#388;&#480;&#530;</pojo>\n";

        final var actual = parserEncodeUTF8.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeDomUTF8Characters() {
        final var inputDom = newElement("pojo").text("ƑƟƠƄǠȒ").build();
        final var expected = "<pojo>ƑƟƠƄǠȒ</pojo>\n";

        final var actual = parserDefault.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeDomUTF8Characters() {
        final var inputXml = "<pojo>&#401;&#415;&#416;&#388;&#480;&#530;</pojo>\n";
        final var expected = newElement("pojo").text("ƑƟƠƄǠȒ").build();

        final var actual = parserEncodeUTF8.fromXml(inputXml);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid deserialized output", expected.name, actual.name);
        assertEquals("Invalid deserialized output", expected.getText(), actual.getText());
    }

}
