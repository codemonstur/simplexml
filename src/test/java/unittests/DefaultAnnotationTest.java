package unittests;

import model.NullDefaultsPojo;
import model.PojoWithDefaults;
import model.RecordWithDefaults;
import model.SimpleWithDefaultsPojo;
import model.SimpleWithDefaultsRecord;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlDefaultValue;
import xmlparser.annotations.XmlTextNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        final var xml = parser.toXml(new PojoWithDefaults());
        assertEquals(EXPECTED_SERIALIZE_CLASS, xml);
    }
    @Test
    public void testSerializeRecord() {
        final var xml = parser.toXml(new RecordWithDefaults(null, null, null));
        assertEquals(EXPECTED_SERIALIZE_RECORD, xml);
    }

    @Test
    public void testDeserializeClass() {
        final var pojo = parser.fromXml("<pojowithdefaults></pojowithdefaults>", PojoWithDefaults.class);

        assertEquals("hello", pojo.field1);
        assertNull(pojo.field2);
        assertEquals("hello", pojo.field3);
    }
    @Test
    public void testDeserializeRecord() {
        final var pojo = parser.fromXml("<recordwithdefaults></recordwithdefaults>", PojoWithDefaults.class);

        assertEquals("hello", pojo.field1);
        assertNull(pojo.field2);
        assertEquals("hello", pojo.field3);
    }

    @Test
    public void testDeserializeSimpleTypesClass() {
        final var pojo = parser.fromXml("<simplewithdefaultspojo></simplewithdefaultspojo>", SimpleWithDefaultsPojo.class);

        assertEquals(Character.valueOf('a'), pojo.character);
        assertEquals(Byte.valueOf("10"), pojo.bytes);
        assertEquals(Integer.valueOf("1"), pojo.integer);
        assertEquals(Short.valueOf((short)1), pojo.shorts);
        assertEquals(Long.valueOf(1L), pojo.longs);
        assertEquals(Boolean.TRUE, pojo.booleans);
        assertEquals("hello", pojo.string);
    }
    @Test
    public void testDeserializeSimpleTypesRecord() {
        final var pojo = parser.fromXml("<simplewithdefaultsrecord></simplewithdefaultsrecord>", SimpleWithDefaultsRecord.class);

        assertEquals(Character.valueOf('a'), pojo.character());
        assertEquals(Byte.valueOf("10"), pojo.bytes());
        assertEquals(Integer.valueOf("1"), pojo.integer());
        assertEquals(Short.valueOf((short)1), pojo.shorts());
        assertEquals(Long.valueOf(1L), pojo.longs());
        assertEquals(Boolean.TRUE, pojo.booleans());
        assertEquals("hello", pojo.string());
    }

    @Test
    public void testSerializeSimpleTypesClass() {
        final var xml = parser.toXml(new SimpleWithDefaultsPojo());
        assertEquals(EXPECTED_SIMPLE_VALUES_CLASS, xml);
    }
    @Test
    public void testSerializeSimpleTypesRecord() {
        final var xml = parser.toXml(new SimpleWithDefaultsRecord(null, null, null, null, null, null, null));
        assertEquals(EXPECTED_SIMPLE_VALUES_RECORD, xml);
    }

    @Test
    public void testSerializeNullsWithDefaults() {
        final var xml = parser.toXml(new NullDefaultsPojo(null, null, null, null, null));

        assertEquals("Invalid serialized output", EXPECTED_NULL_DEFAULTS, xml);
    }
    @Test
    public void testDeserializeNullsWithDefaults() {
        final var pojo = parser.fromXml(EXPECTED_NULL_DEFAULTS, NullDefaultsPojo.class);

        assertEquals("Attribute default is missing", Integer.valueOf(5), pojo.attribute);
        assertEquals("Wrapped default is missing", "hello", pojo.wrapped);
        assertEquals("Default is missing", "body", pojo.body);
        assertEquals("Default is missing", "tail", pojo.tail);
        assertEquals("Text node default is missing", "text", pojo.text);
    }
    @Test
    public void testSerializeTextNodeWithDefaultValue() {
        final var xml = parser.toXml(new TextOnly());
        final var expected = """
                <textonly>only</textonly>
                """;

        assertEquals("Invalid serialized output", expected, xml);
    }

    private static class TextOnly {
        @XmlDefaultValue("only")
        @XmlTextNode
        public String text;
    }

}
