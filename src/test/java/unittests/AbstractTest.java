package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlAbstractClass;
import xmlparser.annotations.XmlAbstractClass.TypeMap;
import xmlparser.annotations.XmlName;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static util.IO.resourceToString;

public class AbstractTest {

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
            @TypeMap(name="class.path.from.external.application.Foo", type=Foo.class)
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

    private XmlParser parser = new XmlParser();

    @Test
    public void deserializeOne() throws IOException {
        final String pojoXml = resourceToString("/abstract_class_1.xml");

        final TargetedMessage1 pojo = parser.fromXml(pojoXml, TargetedMessage1.class);

        assertNotNull("No serialization response", pojo);
        assertEquals("Field 'sender' is wrong", "external application", pojo.sender);
        assertNotNull("Missing 'payload' field", pojo.payload);
        assertEquals("Payload field has wrong type", pojo.payload.getClass(), Foo.class);
        assertEquals("Foo does not have 'id' set", pojo.payload.getId(), Integer.valueOf(1));
    }

    @Test
    public void deserializeTwo() throws IOException {
        final String pojoXml = resourceToString("/abstract_class_2.xml");

        final TargetedMessage2 pojo = parser.fromXml(pojoXml, TargetedMessage2.class);

        assertNotNull("No serialization response", pojo);
        assertEquals("Field 'sender' is wrong", "external application", pojo.sender);
        assertNotNull("Missing 'payload' field", pojo.payload);
        assertEquals("Payload field has wrong type", pojo.payload.getClass(), Foo.class);
        assertEquals("Foo does not have 'id' set", pojo.payload.getId(), Integer.valueOf(1));
    }

    @Test
    public void deserializeThree() throws IOException {
        final String pojoXml = resourceToString("/abstract_class_3.xml");

        final TargetedMessage3 pojo = parser.fromXml(pojoXml, TargetedMessage3.class);

        assertNotNull("No serialization response", pojo);
        assertEquals("Field 'sender' is wrong", "external application", pojo.sender);
        assertNotNull("Missing 'payload' field", pojo.payload);
        assertEquals("Payload field has wrong type", pojo.payload.getClass(), Foo.class);
        assertEquals("Foo does not have 'id' set", pojo.payload.getId(), Integer.valueOf(1));
    }
}
