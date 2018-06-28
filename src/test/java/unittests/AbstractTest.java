package unittests;

import org.junit.Test;
import simplexml.SimpleXml;
import simplexml.model.XmlAbstractClass;
import simplexml.model.XmlAbstractClass.TypeMap;
import simplexml.model.XmlName;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static util.IO.resourceToString;

public class AbstractTest {

    @XmlName("targetedMessage")
    public class TargetedMessage {
        String sender;
        @XmlAbstractClass(attribute="class", types={
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

    private SimpleXml simple = new SimpleXml();

    @Test
    public void deserialize() throws IOException {
        final String pojoXml = resourceToString("/abstractclass.xml");

        final TargetedMessage pojo = simple.fromXml(pojoXml, TargetedMessage.class);

        assertNotNull("No serialization response", pojo);
        assertEquals("Field 'sender' is wrong", "external application", pojo.sender);
        assertNotNull("Missing 'payload' field", pojo.payload);
        assertEquals("Payload field has wrong type", pojo.payload.getClass(), Foo.class);
        assertEquals("Foo does not have 'id' set", pojo.payload.getId(), Integer.valueOf(1));
    }
}
