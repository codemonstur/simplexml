package unittests;

import model.ValidatorChildPojo;
import model.ValidatorInterfacePojo;
import model.ValidatorOverridingPojo;
import model.ValidatorPojo;
import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.XmlObjectValidator;
import xmlparser.error.InvalidObject;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void deserializeValid() {
        final var input = "<testablepojo>\n  <name>Hello</name>\n</testablepojo>\n";

        final var actual = parser.fromXml(input, ValidatorPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void deserializeInvalid() {
        final var input = "<testablepojo>\n  <name>invalid</name>\n</testablepojo>\n";

        assertThrows(InvalidObject.class, () ->
            parser.fromXml(input, ValidatorPojo.class),
            "Invalid object was created");
    }

    @Test
    public void deserializeValidWithInheritedValidator() {
        final var actual = parser.fromXml("<x><name>Hello</name></x>", ValidatorChildPojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void deserializeInvalidWithInheritedValidator() {
        assertThrows(InvalidObject.class, () ->
            parser.fromXml("<x><name>invalid</name></x>", ValidatorChildPojo.class),
            "Inherited validator was not run");
    }

    @Test
    public void deserializeInvalidWithOverriddenValidator() {
        assertThrows(InvalidObject.class, () ->
            parser.fromXml("<x><name>overriding</name></x>", ValidatorOverridingPojo.class),
            "Overriding validator was not run");
    }

    @Test
    public void overridingValidatorStillChecksTheSuperclass() {
        assertThrows(InvalidObject.class, () ->
            parser.fromXml("<x><name>invalid</name></x>", ValidatorOverridingPojo.class),
            "Validator of the superclass was not run by the override");
    }

    @Test
    public void deserializeInvalidWithInterfaceValidator() {
        assertThrows(InvalidObject.class, () ->
            parser.fromXml("<x><name>invalid</name></x>", ValidatorInterfacePojo.class),
            "Validator declared on an interface was not run");
    }

    @Test
    public void deserializeValidWithInterfaceValidator() {
        final var actual = parser.fromXml("<x><name>Hello</name></x>", ValidatorInterfacePojo.class);

        assertNotNull(actual, "Pojo is null");
        assertEquals("Hello", actual.name, "Pojo has the wrong name");
    }

    @Test
    public void overriddenValidatorRunsOnlyOnce() {
        calls.set(0);

        parser.fromXml("<x><name>a</name></x>", CountingChild.class);

        assertEquals(1, calls.get(), "Overriding validator was run more than once");
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
