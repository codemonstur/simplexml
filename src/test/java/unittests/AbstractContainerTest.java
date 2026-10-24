package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlAbstractClass;
import xmlparser.annotations.XmlAbstractClass.TypeMap;
import xmlparser.annotations.XmlName;
import xmlparser.annotations.XmlWrapperTag;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    public static abstract class Item {
        public Integer id;
    }
    public static class Foo extends Item {
        String board_field;
    }
    public static class Bar extends Item {
        String role_field;
    }

    private final XmlParser parser = new XmlParser();

    private static final String xml = """
            <items>
              <foo>
                <Item>
                    <id>56789012</id>
                    <type>board</type>
                    <board_field>098765</board_field>
                </Item>
                <Item>
                    <id>1234</id>
                    <type>role</type>
                    <role_field>90000</role_field>
                </Item>
              </foo>
            </items>""";

    @Test
    public void deserializeList() {
        final var actual = parser.fromXml(xml, ContainerList.class);

        assertNotNull(actual, "No serialization response");
        assertNotNull(actual.items, "Missing 'items' field");
        assertEquals(Foo.class, actual.items.get(0).getClass(), "List item 0 has wrong type");
        assertEquals(Bar.class, actual.items.get(1).getClass(), "List item 1 has wrong type");
        assertEquals("098765", ((Foo)actual.items.get(0)).board_field, "Foo does not have 'board_field' set");
        assertEquals("90000", ((Bar)actual.items.get(1)).role_field, "Bar does not have 'role_field' set");
    }

    @Test
    public void deserializeSet() {
        final var actual = parser.fromXml(xml, ContainerSet.class);

        assertNotNull(actual, "No serialization response");
        assertNotNull(actual.items, "Missing 'items' field");
        assertEquals(2, actual.items.size(), "Invalid set size");
        final Foo foo = findFirstItemOfType(actual.items, Foo.class);
        assertNotNull(foo, "Missing 'foo' instance");
        assertEquals("098765", foo.board_field, "Foo does not have 'board_field' set");

        final Bar bar = findFirstItemOfType(actual.items, Bar.class);
        assertNotNull(bar, "Missing 'bar' instance");
        assertEquals("90000", bar.role_field, "Bar does not have 'role_field' set");
    }

    private static <T> T findFirstItemOfType(final Set<?> set, final Class<T> type) {
        for (final Object o : set) {
            if (o.getClass().equals(type))
                return type.cast(o);
        }
        return null;
    }

}
