package unittests;

import model.StaticFieldPojo;
import org.junit.Test;
import simplexml.SimpleXml;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class StaticFieldTest {

    private SimpleXml simple = new SimpleXml();

    @Test
    public void deserializeObject() throws IOException {
        final String pojoXml = "<staticfieldpojo></staticfieldpojo>\n";

        final StaticFieldPojo pojo = simple.fromXml(pojoXml, StaticFieldPojo.class);

        assertNotNull("Pojo is null", pojo);
    }

}
