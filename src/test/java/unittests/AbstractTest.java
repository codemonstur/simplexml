package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlAbstractClass;
import xmlparser.annotations.XmlAbstractClass.TypeMap;
import xmlparser.annotations.XmlName;
import xmlparser.error.InvalidAnnotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public class TargetedMessage1 {
        String sender;
        @XmlAbstractClass(types={
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class)
        })
        Payload payload;
    }

    @XmlName("targetedMessage")
    public class TargetedMessage2 {
        String sender;
        @XmlAbstractClass(attribute="type", types={
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class),
            @TypeMap(name="class.path.from.external.application.Bar", type=Bar.class)
        })
        Payload payload;
    }
    @XmlName("targetedMessage")
    public class TargetedMessage3 {
        String sender;
        @XmlAbstractClass(tag="class", types={
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class)
        })
        Payload payload;
    }

    abstract class Payload {
        private Integer id;
        Integer getId() {
            return id;
        }
    }
    public class Foo extends Payload {}
    public class Bar extends Payload {
        String name;
    }

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeOne() {
        final var actual = parser.fromXml(ABSTRACT_CLASS_1, TargetedMessage1.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Field 'sender' is wrong", "external application", actual.sender);
        assertNotNull("Missing 'payload' field", actual.payload);
        assertEquals("Payload field has wrong type", Foo.class, actual.payload.getClass());
        assertEquals("Foo does not have 'id' set", Integer.valueOf(1), actual.payload.getId());
    }

    @Test
    public void deserializeTwo() {
        final var actual = parser.fromXml(ABSTRACT_CLASS_2, TargetedMessage2.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Field 'sender' is wrong", "external application", actual.sender);
        assertNotNull("Missing 'payload' field", actual.payload);
        assertEquals("Payload field has wrong type", Foo.class, actual.payload.getClass());
        assertEquals("Foo does not have 'id' set", Integer.valueOf(1), actual.payload.getId());
    }

    @Test
    public void deserializeThree() {
        final var actual = parser.fromXml(ABSTRACT_CLASS_3, TargetedMessage3.class);

        assertNotNull("No serialization response", actual);
        assertEquals("Field 'sender' is wrong", "external application", actual.sender);
        assertNotNull("Missing 'payload' field", actual.payload);
        assertEquals("Payload field has wrong type", Foo.class, actual.payload.getClass());
        assertEquals("Foo does not have 'id' set", Integer.valueOf(1), actual.payload.getId());
    }

    @Test(expected = InvalidAnnotation.class)
    public void missingTypeThrowsException() {
        parser.fromXml(ABSTRACT_MISSING_CLASS, TargetedMessage1.class);
    }

    @Test(expected = InvalidAnnotation.class)
    public void missingAttributeThrowsException() {
        parser.fromXml(ABSTRACT_CLASS_3, TargetedMessage1.class);
    }

    @Test(expected = InvalidAnnotation.class)
    public void missingChildTagThrowsException() {
        parser.fromXml(ABSTRACT_CLASS_1, TargetedMessage3.class);
    }

}
