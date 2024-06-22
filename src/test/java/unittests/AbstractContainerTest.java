package unittests;

import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlAbstractClass;
import xmlparser.annotations.XmlAbstractClass.TypeMap;
import xmlparser.annotations.XmlName;
import xmlparser.annotations.XmlWrapperTag;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractContainerTest {

    @XmlName("items")
    public static class ContainerList {
        @XmlWrapperTag("foo")
        @XmlName("Item")
        @XmlAbstractClass(tag="type", types={
            @TypeMap(name="board", type=Foo.class),
            @TypeMap(name="role", type=Bar.class)
        })
        public List<Item> items;
    }

    @XmlName("items")
    public static class ContainerSet {
        @XmlWrapperTag("foo")
        @XmlName("Item")
        @XmlAbstractClass(tag="type", types={
                @TypeMap(name="board", type=Foo.class),
                @TypeMap(name="role", type=Bar.class)
        })
        public Set<Item> items;
    }

    abstract class Item {
        public Integer id;
    }
    public class Foo extends Item {
        String board_field;
    }
    public class Bar extends Item {
        String role_field;
    }

    private final XmlParser parser = new XmlParser();

    private static final String xml = """
            <items>
              <foo>
                <Item>
                    <id>56789012</content_id>
                    <type>board</type>
                    <board_field>098765</board_field>
                </Item>
                <Item>
                    <id>1234</content_id>
                    <type>role</type>
                    <role_field>90000</role_field>
                </Item>
              </foo>
            </items>""";

    @Test
    public void deserializeList() {
        final var actual = parser.fromXml(xml, ContainerList.class);

        assertNotNull("No serialization response", actual);
        assertNotNull("Missing 'items' field", actual.items);
        assertEquals("List item 0 has wrong type", Foo.class, actual.items.get(0).getClass());
        assertEquals("List item 1 has wrong type", Bar.class, actual.items.get(1).getClass());
        assertEquals("Foo does not have 'board_field' set", "098765", ((Foo)actual.items.get(0)).board_field);
        assertEquals("Bar does not have 'role_field' set", "90000", ((Bar)actual.items.get(1)).role_field);
    }

    @Test
    public void deserializeSet() {
        final var actual = parser.fromXml(xml, ContainerSet.class);

        assertNotNull("No serialization response", actual);
        assertNotNull("Missing 'items' field", actual.items);
        assertEquals("Invalid set size", 2, actual.items.size());
        final Foo foo = findFirstItemOfType(actual.items, Foo.class);
        assertNotNull("Missing 'foo' instance", foo);
        assertEquals("Foo does not have 'board_field' set", "098765", foo.board_field);

        final Bar bar = findFirstItemOfType(actual.items, Bar.class);
        assertNotNull("Missing 'bar' instance", bar);
        assertEquals("Bar does not have 'role_field' set", "90000", bar.role_field);
    }

    private static <T> T findFirstItemOfType(final Set<?> set, final Class<T> type) {
        for (final Object o : set) {
            if (o.getClass().equals(type))
                return type.cast(o);
        }
        return null;
    }

}
