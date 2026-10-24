package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlAbstractClass;
import xmlparser.annotations.XmlAbstractClass.TypeMap;
import xmlparser.annotations.XmlName;
import xmlparser.error.InvalidAnnotation;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractTest {

    private static final String ABSTRACT_CLASS_1 = """
            <targetedMessage>
                <sender>external application</sender>
                <payload class="class.path.from.external.application.Foo">
                    <id>1</id>
                </payload>
            </targetedMessage>""";

    private static final String ABSTRACT_CLASS_2 = """
            <targetedMessage>
                <sender>external application</sender>
                <payload type="class.path.from.external.application.Foo">
                    <id>1</id>
                </payload>
            </targetedMessage>""";

    private static final String ABSTRACT_CLASS_3 = """
            <targetedMessage>
                <sender>external application</sender>
                <payload>
                    <class>class.path.from.external.application.Foo</class>
                    <id>1</id>
                </payload>
            </targetedMessage>""";

    private static final String ABSTRACT_MISSING_CLASS = """
            <targetedMessage>
                <sender>external application</sender>
                <payload class="class.path.from.external.application.Bar">
                    <id>1</id>
                </payload>
            </targetedMessage>""";

    @XmlName("targetedMessage")
    public static class TargetedMessage1 {
        String sender;
        @XmlAbstractClass(types={
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class)
        })
        Payload payload;
    }

    @XmlName("targetedMessage")
    public static class TargetedMessage2 {
        String sender;
        @XmlAbstractClass(attribute="type", types={
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class),
            @TypeMap(name="class.path.from.external.application.Bar", type=Bar.class)
        })
        Payload payload;
    }
    @XmlName("targetedMessage")
    public static class TargetedMessage3 {
        String sender;
        @XmlAbstractClass(tag="class", types={
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class)
        })
        Payload payload;
    }

    abstract static class Payload {
        private Integer id;
        Integer getId() {
            return id;
        }
    }
    public static class Foo extends Payload {}
    public static class Bar extends Payload {
        String name;
    }

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeOne() {
        final var actual = parser.fromXml(ABSTRACT_CLASS_1, TargetedMessage1.class);

        assertNotNull(actual, "No serialization response");
        assertEquals("external application", actual.sender, "Field 'sender' is wrong");
        assertNotNull(actual.payload, "Missing 'payload' field");
        assertEquals(Foo.class, actual.payload.getClass(), "Payload field has wrong type");
        assertEquals(Integer.valueOf(1), actual.payload.getId(), "Foo does not have 'id' set");
    }

    @Test
    public void deserializeTwo() {
        final var actual = parser.fromXml(ABSTRACT_CLASS_2, TargetedMessage2.class);

        assertNotNull(actual, "No serialization response");
        assertEquals("external application", actual.sender, "Field 'sender' is wrong");
        assertNotNull(actual.payload, "Missing 'payload' field");
        assertEquals(Foo.class, actual.payload.getClass(), "Payload field has wrong type");
        assertEquals(Integer.valueOf(1), actual.payload.getId(), "Foo does not have 'id' set");
    }

    @Test
    public void deserializeThree() {
        final var actual = parser.fromXml(ABSTRACT_CLASS_3, TargetedMessage3.class);

        assertNotNull(actual, "No serialization response");
        assertEquals("external application", actual.sender, "Field 'sender' is wrong");
        assertNotNull(actual.payload, "Missing 'payload' field");
        assertEquals(Foo.class, actual.payload.getClass(), "Payload field has wrong type");
        assertEquals(Integer.valueOf(1), actual.payload.getId(), "Foo does not have 'id' set");
    }

    @Test
    public void missingTypeThrowsException() {
        assertThrows(InvalidAnnotation.class, () ->
            parser.fromXml(ABSTRACT_MISSING_CLASS, TargetedMessage1.class));
    }

    @Test
    public void missingAttributeThrowsException() {
        assertThrows(InvalidAnnotation.class, () ->
            parser.fromXml(ABSTRACT_CLASS_3, TargetedMessage1.class));
    }

    @Test
    public void missingChildTagThrowsException() {
        assertThrows(InvalidAnnotation.class, () ->
            parser.fromXml(ABSTRACT_CLASS_1, TargetedMessage3.class));
    }

}
