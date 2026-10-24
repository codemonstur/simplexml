package unittests;

import model.StaticFieldPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StaticFieldTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeObject() {
        final var input = "<staticfieldpojo></staticfieldpojo>\n";

        final var actual = parser.fromXml(input, StaticFieldPojo.class);

        assertNotNull(actual, "Pojo is null");
    }

}
