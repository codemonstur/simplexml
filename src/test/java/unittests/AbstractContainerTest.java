package unittests;

import org.junit.Test;
import simplexml.SimpleXml;
import simplexml.annotations.XmlAbstractClass;
import simplexml.annotations.XmlAbstractClass.TypeMap;
import simplexml.annotations.XmlName;
import simplexml.annotations.XmlWrapperTag;

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

    private SimpleXml simple = new SimpleXml();

    public static final String xml =
            "<items>\n" +
            "  <foo>\n" +
            "    <Item>\n" +
            "        <id>56789012</content_id>\n" +
            "        <type>board</type>\n" +
            "        <board_field>098765</board_field>\n" +
            "    </Item>\n" +
            "    <Item>\n" +
            "        <id>1234</content_id>\n" +
            "        <type>role</type>\n" +
            "        <role_field>90000</role_field>\n" +
            "    </Item>\n" +
            "  </foo>\n" +
            "</items>";

    @Test
    public void deserializeList() {
        final ContainerList pojo = simple.fromXml(xml, ContainerList.class);

        assertNotNull("No serialization response", pojo);
        assertNotNull("Missing 'items' field", pojo.items);
        assertEquals("List item 0 has wrong type", pojo.items.get(0).getClass(), Foo.class);
        assertEquals("List item 1 has wrong type", pojo.items.get(1).getClass(), Bar.class);
        assertEquals("Foo does not have 'board_field' set", ((Foo)pojo.items.get(0)).board_field, "098765");
        assertEquals("Bar does not have 'role_field' set", ((Bar)pojo.items.get(1)).role_field, "90000");
    }

    @Test
    public void deserializeSet() {
        final ContainerSet pojo = simple.fromXml(xml, ContainerSet.class);

        assertNotNull("No serialization response", pojo);
        assertNotNull("Missing 'items' field", pojo.items);
        assertEquals("Invalid set size", 2, pojo.items.size());
        final Foo foo = findFirstItemOfType(pojo.items, Foo.class);
        assertNotNull("Missing 'foo' instance", foo);
        assertEquals("Foo does not have 'board_field' set", foo.board_field, "098765");

        final Bar bar = findFirstItemOfType(pojo.items, Bar.class);
        assertNotNull("Missing 'bar' instance", bar);
        assertEquals("Bar does not have 'role_field' set", bar.role_field, "90000");
    }

    private static <T> T findFirstItemOfType(final Set<?> set, final Class<T> type) {
        for (final Object o : set) {
            if (o.getClass().equals(type))
                return type.cast(o);
        }
        return null;
    }
}
