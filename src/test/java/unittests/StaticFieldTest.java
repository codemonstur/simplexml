package unittests;

import model.StaticFieldPojo;
import org.junit.Test;
import simplexml.SimpleXml;

import static org.junit.Assert.assertNotNull;

public class StaticFieldTest {

    private SimpleXml simple = new SimpleXml();

    @Test
    public void deserializeObject() {
        final String pojoXml = "<staticfieldpojo></staticfieldpojo>\n";

        final StaticFieldPojo pojo = simple.fromXml(pojoXml, StaticFieldPojo.class);

        assertNotNull("Pojo is null", pojo);
    }

}
