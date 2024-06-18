package unittests;

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
        final XmlElement inputDom = newElement("test").text("&<>'\"").build();
        final String expected = "<test>&amp;&lt;&gt;&apos;&quot;</test>\n";

        final String actual = parserDefault.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializeDomWithDangerousCharsInAttribute() {
        final XmlElement inputDom = newElement("test").attribute("hello", "&<>'\"").build();
        final String expected = "<test hello=\"&amp;&lt;&gt;&apos;&quot;\" />\n";

        final String actual = parserDefault.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeDomWithEscapedChars() {
        final String inputXml = "<pojo>&lt;&gt;&amp; and something &quot; &apos; &apos;</pojo>\n";
        final XmlElement expected = newElement("pojo").text("<>& and something \" ' '").build();

        final XmlElement actual = parserDefault.fromXml(inputXml);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid deserialized output", expected.name, actual.name);
        assertEquals("Invalid deserialized output", expected.getText(), actual.getText());
    }

    @Test
    public void serializeDomUTF8Characters() {
        final XmlElement inputDom = newElement("pojo").text("ƑƟƠƄǠȒ").build();
        final String expected = "<pojo>&#401;&#415;&#416;&#388;&#480;&#530;</pojo>\n";

        final String actual = parserEncodeUTF8.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeDomUTF8Characters() {
        final XmlElement inputDom = newElement("pojo").text("ƑƟƠƄǠȒ").build();
        final String expected = "<pojo>ƑƟƠƄǠȒ</pojo>\n";

        final String actual = parserDefault.domToXml(inputDom);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeDomUTF8Characters() {
        final String inputXml = "<pojo>&#401;&#415;&#416;&#388;&#480;&#530;</pojo>\n";
        final XmlElement expected = newElement("pojo").text("ƑƟƠƄǠȒ").build();

        final XmlElement actual = parserEncodeUTF8.fromXml(inputXml);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid deserialized output", expected.name, actual.name);
        assertEquals("Invalid deserialized output", expected.getText(), actual.getText());
    }

}

