package unittests;

import model.Pojo;
import model.PojoWithAttribute;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlMapWithChildNodes;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static utils.Functions.mapOf;
import static xmlparser.XmlParser.newXmlParser;

public class EscapeMapWithChildNodesTest {

    private final XmlParser parserDefault = new XmlParser();
    private final XmlParser parserEncodeUTF8 = newXmlParser().shouldEncodeUTF8().build();

    private static class PojoMap {
        @XmlMapWithChildNodes(keyName = "key")
        private final Map<String, String> map1;
        @XmlMapWithChildNodes(keyName = "key", valueName = "value")
        private final Map<String, String> map2;

        private PojoMap(final Map<String, String> map1, final Map<String, String> map2) {
            this.map1 = map1;
            this.map2 = map2;
        }
    }

    @Test
    public void serializeMap1WithDangerousChars() {
        final PojoMap inputPojo = new PojoMap(mapOf("<>&\"'", "<>&\"'"), null);
        final String expected = "<pojomap>\n" +
                "  <map1>\n" +
                "    <key>&lt;&gt;&amp;&quot;&apos;</key>\n" +
                "    &lt;&gt;&amp;&quot;&apos;\n" +
                "  </map1>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializeMap2WithDangerousChars() {
        final PojoMap inputPojo = new PojoMap(null, mapOf("<>&\"'", "<>&\"'"));
        final String expected = "<pojomap>\n" +
                "  <map2>\n" +
                "    <key>&lt;&gt;&amp;&quot;&apos;</key>\n" +
                "    <value>&lt;&gt;&amp;&quot;&apos;</value>\n" +
                "  </map2>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMap1WithEscapedChars() {
        final String inputXml = "<pojomap>\n" +
                "  <map1>\n" +
                "    <key>&lt;&gt;&amp;&quot;&apos;</key>\n" +
                "    &lt;&gt;&amp;&quot;&apos;\n" +
                "  </map1>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(mapOf("<>&\"'", "<>&\"'"), null);

        final PojoMap actual = parserDefault.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map1.get("<>&\"'"), actual.map1.get("<>&\"'"));
    }

    @Test
    public void deserializeMap2WithEscapedChars() {
        final String inputXml = "<pojomap>\n" +
                "  <map2>\n" +
                "    <key>&lt;&gt;&amp;&quot;&apos;</key>\n" +
                "    <value>&lt;&gt;&amp;&quot;&apos;</value>\n" +
                "  </map2>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(null, mapOf("<>&\"'", "<>&\"'"));

        final PojoMap actual = parserDefault.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map2.get("<>&\"'"), actual.map2.get("<>&\"'"));
    }

    @Test
    public void serializeMap1UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(mapOf("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"), null);
        final String expected = "<pojomap>\n" +
                "  <map1>\n" +
                "    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>\n" +
                "    &#401;&#415;&#416;&#388;&#480;&#530;\n" +
                "  </map1>\n" +
                "</pojomap>\n";

        final String actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeMap1UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(mapOf("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"), null);
        final String expected = "<pojomap>\n" +
                "  <map1>\n" +
                "    <key>ƑƟƠƄǠȒ</key>\n" +
                "    ƑƟƠƄǠȒ\n" +
                "  </map1>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMap1UTF8Characters() {
        final String inputXml = "<pojomap>\n" +
                "  <map1>\n" +
                "    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>\n" +
                "    &#401;&#415;&#416;&#388;&#480;&#530;\n" +
                "  </map1>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(mapOf("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"), null);

        final PojoMap actual = parserEncodeUTF8.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map1.get("ƑƟƠƄǠȒ"), actual.map1.get("ƑƟƠƄǠȒ"));
    }

    @Test
    public void serializeMap2UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(null, mapOf("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"));
        final String expected = "<pojomap>\n" +
                "  <map2>\n" +
                "    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>\n" +
                "    <value>&#401;&#415;&#416;&#388;&#480;&#530;</value>\n" +
                "  </map2>\n" +
                "</pojomap>\n";

        final String actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeMap2UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(null, mapOf("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"));
        final String expected = "<pojomap>\n" +
                "  <map2>\n" +
                "    <key>ƑƟƠƄǠȒ</key>\n" +
                "    <value>ƑƟƠƄǠȒ</value>\n" +
                "  </map2>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMap2UTF8Characters() {
        final String inputXml = "<pojomap>\n" +
                "  <map2>\n" +
                "    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>\n" +
                "    <value>&#401;&#415;&#416;&#388;&#480;&#530;</value>\n" +
                "  </map2>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(null, mapOf("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"));

        final PojoMap actual = parserEncodeUTF8.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map2.get("ƑƟƠƄǠȒ"), actual.map2.get("ƑƟƠƄǠȒ"));
    }

}
