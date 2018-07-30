package unittests;

import model.WrappedPojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class WrappedTest {
    private static final WrappedPojo wrapped = newDefaultWrappedPojo();
    private static final String wrappedXml = "<wrappedpojo>\n" +
            "  <wrapper1>\n" +
            "    <string>Hello</string>\n" +
            "  </wrapper1>\n" +
            "  <wrapper2>\n" +
            "    <list>, world</list>\n" +
            "  </wrapper2>\n" +
            "  <wrapper3>\n" +
            "    <map>\n" +
            "      <Guybrush>Threepwood</Guybrush>\n" +
            "    </map>\n" +
            "  </wrapper3>\n" +
            "  <wrapper4>\n" +
            "    <set>Mêlée Island</set>\n" +
            "  </wrapper4>\n" +
            "  <wrapper5>\n" +
            "    <array>monkey</array>\n" +
            "  </wrapper5>\n" +
            "</wrappedpojo>\n";

    private SimpleXml simple = new SimpleXml();

    @Test
    public void deserialize() throws IOException {
        final WrappedPojo pojo = simple.fromXml(wrappedXml, WrappedPojo.class);

        assertNotNull("Pojo is null", pojo);
        assertReflectionEquals(newDefaultWrappedPojo(), pojo);
    }

    @Test
    public void serialize() {
        final String xml = simple.toXml(wrapped);

        assertNotNull("No serialization response", xml);
        assertEquals("Invalid serialized output", wrappedXml, xml);
    }

    private static WrappedPojo newDefaultWrappedPojo() {
        final Map<String, String> map = new HashMap<>();
        map.put("Guybrush", "Threepwood");
        final Set<String> set = new HashSet<>();
        set.add("Mêlée Island");
        return new WrappedPojo("Hello", Arrays.asList(", world"), map, set, new String[]{"monkey"});
    }
}