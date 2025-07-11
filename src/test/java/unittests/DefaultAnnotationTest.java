package unittests;

import model.PojoWithDefaults;
import model.RecordWithDefaults;
import model.SimpleWithDefaultsPojo;
import model.SimpleWithDefaultsRecord;
import org.junit.Test;
import xmlparser.XmlParser;

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

}
