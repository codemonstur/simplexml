package unittests;

import model.Pojo;
import model.PojoSelfClosed;
import model.PojoWithText;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class SelfClosingTest {

    private final XmlParser parser = newXmlParser().shouldPrettyPrint(false).build();

    @Test
    public void serializeEmpty() {
        final var input = new Pojo("");
        final var expected = "<pojo><name/></pojo>";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializeEmptyWithAttribute() {
        final var input = new PojoSelfClosed("name", "");
        final var expected = "<pojoselfclosed name=\"name\" />";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

    @Test
    public void serializeFilled() {
        final var input = new PojoSelfClosed("name", "text");
        final var expected = "<pojoselfclosed name=\"name\">text</pojoselfclosed>";

        final var actual = parser.toXml(input);

        assertNotNull("No serialization response", actual);
        assertEquals("Invalid serialized output", expected, actual);
    }

}
