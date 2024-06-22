package unittests;

import model.StaticFieldPojo;
import org.junit.Test;
import xmlparser.XmlParser;

import static org.junit.Assert.assertNotNull;

public class StaticFieldTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeObject() {
        final var input = "<staticfieldpojo></staticfieldpojo>\n";

        final var actual = parser.fromXml(input, StaticFieldPojo.class);

        assertNotNull("Pojo is null", actual);
    }

}
