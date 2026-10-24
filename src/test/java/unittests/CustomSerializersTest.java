package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static xmlparser.XmlParser.newXmlParser;

public class CustomSerializersTest {

    private static class Container {
        final String name;
        final Special special;
        private Container(final String name, final Special special) {
            this.name = name;
            this.special = special;
        }
    }

    private static class Special {
        final Integer id;
        final String name;
        private Special(final Integer id, final String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final Container CONTAINER = new Container("container", new Special(1, "special"));
    private static final String CONTAINER_XML = """
            <container>
              <name>container</name>
              <special>1:special</special>
            </container>
            """;

    private final XmlParser parser = newXmlParser()
        .addDeserializer(Special.class, value -> {
            final String[] parts = value.split(":");
            if (parts.length != 2) return null;
            return new Special(Integer.parseInt(parts[0]), parts[1]);
        })
        .addSerializer(Special.class, value -> {
            final Special s = (Special)value;
            return s.id+":"+s.name;
        })
        .build();


    @Test
    public void serialize() {
        final var actual = parser.toXml(CONTAINER);

        assertNotNull(actual, "No serialization response");
        assertEquals(CONTAINER_XML, actual, "Invalid serialized output");
    }

    @Test
    public void deserialize() {
        final var actual = parser.fromXml(CONTAINER_XML, Container.class);

        assertNotNull(actual, "Pojo is null");
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(CONTAINER);
    }

}
