package unittests;

import model.NullDefaultsPojo;
import model.PojoWithDefaults;
import model.RecordWithDefaults;
import model.SimpleWithDefaultsPojo;
import model.SimpleWithDefaultsRecord;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlDefaultValue;
import xmlparser.annotations.XmlTextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DefaultAnnotationTest {

    private static final String
        EXPECTED_SERIALIZE_CLASS = """
            <pojowithdefaults>
              <field1>hello</field1>
              <field2>hello</field2>
            </pojowithdefaults>
            """,
        EXPECTED_SERIALIZE_RECORD = """
            <recordwithdefaults>
              <field1>hello</field1>
              <field2>hello</field2>
            </recordwithdefaults>
            """,
        EXPECTED_SIMPLE_VALUES_CLASS = """
            <simplewithdefaultspojo>
              <integer>1</integer>
              <character>a</character>
              <string>hello</string>
              <bytes>10</bytes>
              <shorts>1</shorts>
              <booleans>true</booleans>
              <longs>1</longs>
            </simplewithdefaultspojo>
            """,
        EXPECTED_SIMPLE_VALUES_RECORD = """
            <simplewithdefaultsrecord>
              <integer>1</integer>
              <character>a</character>
              <string>hello</string>
              <bytes>10</bytes>
              <shorts>1</shorts>
              <booleans>true</booleans>
              <longs>1</longs>
            </simplewithdefaultsrecord>
            """,
        EXPECTED_NULL_DEFAULTS = """
            <nulldefaultspojo attribute="5">
              <wrap>
                <wrapped>hello</wrapped>
              </wrap>
              <body>body</body>
              <tail>tail</tail>
            text
            </nulldefaultspojo>
            """;


    private final XmlParser parser = new XmlParser();

    @Test
    public void testSerializeClass() {
        final var actual = parser.toXml(new PojoWithDefaults());
        assertEquals(EXPECTED_SERIALIZE_CLASS, actual);
    }
    @Test
    public void testSerializeRecord() {
        final var actual = parser.toXml(new RecordWithDefaults(null, null, null));
        assertEquals(EXPECTED_SERIALIZE_RECORD, actual);
    }

    @Test
    public void testDeserializeClass() {
        final var actual = parser.fromXml("<pojowithdefaults></pojowithdefaults>", PojoWithDefaults.class);

        assertEquals("hello", actual.field1);
        assertNull(actual.field2);
        assertEquals("hello", actual.field3);
    }
    @Test
    public void testDeserializeRecord() {
        final var actual = parser.fromXml("<recordwithdefaults></recordwithdefaults>", PojoWithDefaults.class);

        assertEquals("hello", actual.field1);
        assertNull(actual.field2);
        assertEquals("hello", actual.field3);
    }

    @Test
    public void testDeserializeSimpleTypesClass() {
        final var actual = parser.fromXml("<simplewithdefaultspojo></simplewithdefaultspojo>", SimpleWithDefaultsPojo.class);

        assertEquals(Character.valueOf('a'), actual.character);
        assertEquals(Byte.valueOf("10"), actual.bytes);
        assertEquals(Integer.valueOf("1"), actual.integer);
        assertEquals(Short.valueOf((short)1), actual.shorts);
        assertEquals(Long.valueOf(1L), actual.longs);
        assertEquals(Boolean.TRUE, actual.booleans);
        assertEquals("hello", actual.string);
    }
    @Test
    public void testDeserializeSimpleTypesRecord() {
        final var actual = parser.fromXml("<simplewithdefaultsrecord></simplewithdefaultsrecord>", SimpleWithDefaultsRecord.class);

        assertEquals(Character.valueOf('a'), actual.character());
        assertEquals(Byte.valueOf("10"), actual.bytes());
        assertEquals(Integer.valueOf("1"), actual.integer());
        assertEquals(Short.valueOf((short)1), actual.shorts());
        assertEquals(Long.valueOf(1L), actual.longs());
        assertEquals(Boolean.TRUE, actual.booleans());
        assertEquals("hello", actual.string());
    }

    @Test
    public void testSerializeSimpleTypesClass() {
        final var actual = parser.toXml(new SimpleWithDefaultsPojo());
        assertEquals(EXPECTED_SIMPLE_VALUES_CLASS, actual);
    }
    @Test
    public void testSerializeSimpleTypesRecord() {
        final var actual = parser.toXml(new SimpleWithDefaultsRecord(null, null, null, null, null, null, null));
        assertEquals(EXPECTED_SIMPLE_VALUES_RECORD, actual);
    }

    @Test
    public void testSerializeNullsWithDefaults() {
        final var actual = parser.toXml(new NullDefaultsPojo(null, null, null, null, null));

        assertEquals(EXPECTED_NULL_DEFAULTS, actual, "Invalid serialized output");
    }
    @Test
    public void testDeserializeNullsWithDefaults() {
        final var actual = parser.fromXml(EXPECTED_NULL_DEFAULTS, NullDefaultsPojo.class);

        assertEquals(Integer.valueOf(5), actual.attribute, "Attribute default is missing");
        assertEquals("hello", actual.wrapped, "Wrapped default is missing");
        assertEquals("body", actual.body, "Default is missing");
        assertEquals("tail", actual.tail, "Default is missing");
        assertEquals("text", actual.text, "Text node default is missing");
    }
    @Test
    public void testSerializeTextNodeWithDefaultValue() {
        final var actual = parser.toXml(new TextOnly());
        final var expected = """
                <textonly>only</textonly>
                """;

        assertEquals(expected, actual, "Invalid serialized output");
    }

    private static class TextOnly {
        @XmlDefaultValue("only")
        @XmlTextNode
        public String text;
    }

}
