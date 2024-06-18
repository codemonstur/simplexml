package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.annotations.XmlMapWithAttributes;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utils.Functions.mapOf;
import static xmlparser.XmlParser.newXmlParser;

public class EscapeMapTagIsKeyTest {

    private final XmlParser parserDefault = new XmlParser();
    private final XmlParser parserEncodeUTF8 = newXmlParser().shouldEncodeUTF8().build();

    private static class PojoMap {
        @XmlMapTagIsKey
        private final Map<String, String> map;

        private PojoMap(final Map<String, String> map) {
            this.map = map;
        }
    }

    @Test
    public void serializeMapWithDangerousChars() {
        final PojoMap inputPojo = new PojoMap(mapOf("key", "<>&\"'"));
        final String expected = "<pojomap>\n" +
                "  <map>\n" +
                "    <key>&lt;&gt;&amp;&quot;&apos;</key>\n" +
                "  </map>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMapWithEscapedChars() {
        final String inputXml = "<pojomap>\n" +
                "  <map>\n" +
                "    <key>&lt;&gt;&amp;&quot;&apos;</key>\n" +
                "  </map>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(mapOf("key", "<>&\"'"));

        final PojoMap actual = parserDefault.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map.get("key"), actual.map.get("key"));
    }

    @Test
    public void serializeMapUTF8Characters() {
        final PojoMap inputPojo = new PojoMap(mapOf("key", "ƑƟƠƄǠȒ"));
        final String expected = "<pojomap>\n" +
                "  <map>\n" +
                "    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>\n" +
                "  </map>\n" +
                "</pojomap>\n";

        final String actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeMapUTF8Characters() {
        final PojoMap inputPojo = new PojoMap(mapOf("key", "ƑƟƠƄǠȒ"));
        final String expected = "<pojomap>\n" +
                "  <map>\n" +
                "    <key>ƑƟƠƄǠȒ</key>\n" +
                "  </map>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMapUTF8Characters() {
        final String inputXml = "<pojomap>\n" +
                "  <map>\n" +
                "    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>\n" +
                "  </map>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(mapOf("key", "ƑƟƠƄǠȒ"));

        final PojoMap actual = parserEncodeUTF8.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map.get("key"), actual.map.get("key"));
    }

}
