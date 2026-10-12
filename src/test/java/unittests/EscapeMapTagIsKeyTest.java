package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlMapTagIsKey;
import xmlparser.error.InvalidXml;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
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
        final var inputPojo = new PojoMap(Map.of("key", "<>&\"'"));
        final var expected = """
                <pojomap>
                  <map>
                    <key>&lt;&gt;&amp;&quot;&apos;</key>
                  </map>
                </pojomap>
                """;

        final var actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMapWithEscapedChars() {
        final var inputXml = """
                <pojomap>
                  <map>
                    <key>&lt;&gt;&amp;&quot;&apos;</key>
                  </map>
                </pojomap>
                """;
        final var expected = new PojoMap(Map.of("key", "<>&\"'"));

        final var actual = parserDefault.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map.get("key"), actual.map.get("key"));
    }

    @Test
    public void serializeMapUTF8Characters() {
        final PojoMap inputPojo = new PojoMap(Map.of("key", "ƑƟƠƄǠȒ"));
        final String expected = """
                <pojomap>
                  <map>
                    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>
                  </map>
                </pojomap>
                """;

        final String actual = parserEncodeUTF8.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void dontSerializeMapUTF8Characters() {
        final PojoMap inputPojo = new PojoMap(Map.of("key", "ƑƟƠƄǠȒ"));
        final String expected = """
                <pojomap>
                  <map>
                    <key>ƑƟƠƄǠȒ</key>
                  </map>
                </pojomap>
                """;

        final String actual = parserDefault.toXml(inputPojo);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void deserializeMapUTF8Characters() {
        final String inputXml = """
                <pojomap>
                  <map>
                    <key>&#401;&#415;&#416;&#388;&#480;&#530;</key>
                  </map>
                </pojomap>
                """;
        final PojoMap expected = new PojoMap(Map.of("key", "ƑƟƠƄǠȒ"));

        final PojoMap actual = parserEncodeUTF8.fromXml(inputXml, PojoMap.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected.map.get("key"), actual.map.get("key"));
    }

    @Test
    public void serializeMapWithANameThatIsNotAnAsciiWord() {
        final var expected = """
                <pojomap>
                  <map>
                    <my-key.1>v</my-key.1>
                  </map>
                </pojomap>
                """;

        assertEquals("A key that is a valid name was rejected",
                expected, parserDefault.toXml(new PojoMap(Map.of("my-key.1", "v"))));
    }

    @Test
    public void serializeMapWithANamespacedKey() {
        final var expected = """
                <pojomap>
                  <map>
                    <ns:key>v</ns:key>
                  </map>
                </pojomap>
                """;

        assertEquals("A namespaced key was rejected",
                expected, parserDefault.toXml(new PojoMap(Map.of("ns:key", "v"))));
    }

    @Test
    public void serializeMapWithANonAsciiKey() {
        final var expected = """
                <pojomap>
                  <map>
                    <é>v</é>
                  </map>
                </pojomap>
                """;

        assertEquals("A non-ascii key was rejected",
                expected, parserDefault.toXml(new PojoMap(Map.of("é", "v"))));
    }

    @Test
    public void serializeMapWithAKeyThatIsNotAName() {
        assertKeyIsRejected("a<b");
        assertKeyIsRejected("x&y");
        assertKeyIsRejected("a b");
        assertKeyIsRejected("a\"b");
        assertKeyIsRejected("a/>");
        assertKeyIsRejected("a>b");
        assertKeyIsRejected("a=b");
        assertKeyIsRejected("123");
        assertKeyIsRejected("");
    }

    private void assertKeyIsRejected(final String key) {
        try {
            parserDefault.toXml(new PojoMap(Map.of(key, "v")));

            fail("Map key '" + key + "' was written as a tag name");
        } catch (final InvalidXml e) {
            assertEquals("Invalid error message",
                    "Map key '" + key + "' is not a valid tag name", e.getMessage());
        }
    }

}
