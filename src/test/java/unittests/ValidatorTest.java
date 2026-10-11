package unittests;

import model.ValidatorChildPojo;
import model.ValidatorInterfacePojo;
import model.ValidatorOverridingPojo;
import model.ValidatorPojo;
import org.junit.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlObjectValidator;
import xmlparser.error.InvalidObject;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ValidatorTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeValid() {
        final var input = "<testablepojo>\n  <name>Hello</name>\n</testablepojo>\n";

        final var actual = parser.fromXml(input, ValidatorPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalid() {
        final var input = "<testablepojo>\n  <name>invalid</name>\n</testablepojo>\n";

        parser.fromXml(input, ValidatorPojo.class);

        fail("Invalid object was created");
    }

    @Test
    public void deserializeValidWithInheritedValidator() {
        final var actual = parser.fromXml("<x><name>Hello</name></x>", ValidatorChildPojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalidWithInheritedValidator() {
        parser.fromXml("<x><name>invalid</name></x>", ValidatorChildPojo.class);

        fail("Inherited validator was not run");
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalidWithOverriddenValidator() {
        parser.fromXml("<x><name>overriding</name></x>", ValidatorOverridingPojo.class);

        fail("Overriding validator was not run");
    }

    @Test(expected = InvalidObject.class)
    public void overridingValidatorStillChecksTheSuperclass() {
        parser.fromXml("<x><name>invalid</name></x>", ValidatorOverridingPojo.class);

        fail("Validator of the superclass was not run by the override");
    }

    @Test(expected = InvalidObject.class)
    public void deserializeInvalidWithInterfaceValidator() {
        parser.fromXml("<x><name>invalid</name></x>", ValidatorInterfacePojo.class);

        fail("Validator declared on an interface was not run");
    }

    @Test
    public void deserializeValidWithInterfaceValidator() {
        final var actual = parser.fromXml("<x><name>Hello</name></x>", ValidatorInterfacePojo.class);

        assertNotNull("Pojo is null", actual);
        assertEquals("Pojo has the wrong name", "Hello", actual.name);
    }

    @Test
    public void overriddenValidatorRunsOnlyOnce() {
        calls.set(0);

        parser.fromXml("<x><name>a</name></x>", CountingChild.class);

        assertEquals("Overriding validator was run more than once", 1, calls.get());
    }

    static final AtomicInteger calls = new AtomicInteger();

    public static class CountingBase {
        public String name;
        @XmlObjectValidator
        public void validate() { calls.incrementAndGet(); }
    }

    public static class CountingChild extends CountingBase {
        @Override
        @XmlObjectValidator
        public void validate() { calls.incrementAndGet(); }
    }

}
