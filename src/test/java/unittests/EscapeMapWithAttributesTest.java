package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlMapWithAttributes;
import xmlparser.annotations.XmlMapWithChildNodes;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class EscapeMapWithAttributesTest {

    private final XmlParser parserDefault = new XmlParser();
    private final XmlParser parserEncodeUTF8 = newXmlParser().shouldEncodeUTF8().build();

    private static class PojoMap {
        @XmlMapWithAttributes(keyName = "key")
        private final Map<String, String> map1;
        @XmlMapWithAttributes(keyName = "key", valueName = "value")
        private final Map<String, String> map2;

        private PojoMap(final Map<String, String> map1, final Map<String, String> map2) {
            this.map1 = map1;
            this.map2 = map2;
        }
    }

    @Test
    public void serializeMap1WithDangerousChars() {
        final PojoMap inputPojo = new PojoMap(Map.of("<>&\"'", "<>&\"'"), null);
        final String expected = "<pojomap>\n" +
                "  <map1 key=\"&lt;&gt;&amp;&quot;&apos;\">&lt;&gt;&amp;&quot;&apos;</map1>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializeMap2WithDangerousChars() {
        final PojoMap inputPojo = new PojoMap(null, Map.of("<>&\"'", "<>&\"'"));
        final String expected = "<pojomap>\n" +
                "  <map2 key=\"&lt;&gt;&amp;&quot;&apos;\" value=\"&lt;&gt;&amp;&quot;&apos;\" />\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMap1WithEscapedChars() {
        final String inputXml = "<pojomap>\n" +
                "  <map1 key=\"&lt;&gt;&amp;&quot;&apos;\">&lt;&gt;&amp;&quot;&apos;</map1>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(Map.of("<>&\"'", "<>&\"'"), null);

        final PojoMap actual = parserDefault.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map1.get("<>&\"'"), actual.map1.get("<>&\"'"));
    }

    @Test
    public void deserializeMap2WithEscapedChars() {
        final String inputXml = "<pojomap>\n" +
                "  <map2 key=\"&lt;&gt;&amp;&quot;&apos;\" value=\"&lt;&gt;&amp;&quot;&apos;\" />\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(null, Map.of("<>&\"'", "<>&\"'"));

        final PojoMap actual = parserDefault.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map2.get("<>&\"'"), actual.map2.get("<>&\"'"));
    }

    @Test
    public void serializeMap1UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(Map.of("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"), null);
        final String expected = "<pojomap>\n" +
                "  <map1 key=\"&#401;&#415;&#416;&#388;&#480;&#530;\">&#401;&#415;&#416;&#388;&#480;&#530;</map1>\n" +
                "</pojomap>\n";

        final String actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeMap1UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(Map.of("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"), null);
        final String expected = "<pojomap>\n" +
                "  <map1 key=\"ƑƟƠƄǠȒ\">ƑƟƠƄǠȒ</map1>\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMap1UTF8Characters() {
        final String inputXml = "<pojomap>\n" +
                "  <map1 key=\"&#401;&#415;&#416;&#388;&#480;&#530;\">&#401;&#415;&#416;&#388;&#480;&#530;</map1>\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(Map.of("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"), null);

        final PojoMap actual = parserEncodeUTF8.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map1.get("ƑƟƠƄǠȒ"), actual.map1.get("ƑƟƠƄǠȒ"));
    }

    @Test
    public void serializeMap2UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(null, Map.of("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"));
        final String expected = "<pojomap>\n" +
                "  <map2 key=\"&#401;&#415;&#416;&#388;&#480;&#530;\" value=\"&#401;&#415;&#416;&#388;&#480;&#530;\" />\n" +
                "</pojomap>\n";

        final String actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeMap2UTF8Characters() {
        final PojoMap inputPojo = new PojoMap(null, Map.of("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"));
        final String expected = "<pojomap>\n" +
                "  <map2 key=\"ƑƟƠƄǠȒ\" value=\"ƑƟƠƄǠȒ\" />\n" +
                "</pojomap>\n";

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMap2UTF8Characters() {
        final String inputXml = "<pojomap>\n" +
                "  <map2 key=\"&#401;&#415;&#416;&#388;&#480;&#530;\" value=\"&#401;&#415;&#416;&#388;&#480;&#530;\" />\n" +
                "</pojomap>\n";
        final PojoMap expected = new PojoMap(null, Map.of("ƑƟƠƄǠȒ", "ƑƟƠƄǠȒ"));

        final PojoMap actual = parserEncodeUTF8.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map2.get("ƑƟƠƄǠȒ"), actual.map2.get("ƑƟƠƄǠȒ"));
    }

}
