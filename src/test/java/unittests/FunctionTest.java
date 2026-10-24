package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.error.AssignmentFailure;
import xmlparser.utils.Reflection;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static xmlparser.utils.Functions.trim;
import static xmlparser.utils.Validator.multipleAreTrue;

public class FunctionTest {

    @Test
    public void testMultipleAreTrue() {
        assertFalse(multipleAreTrue(false));
        assertFalse(multipleAreTrue(false, false, false, false));
        assertFalse(multipleAreTrue(false, false, true, false));
        assertFalse(multipleAreTrue(false, false, false, true));
        assertTrue(multipleAreTrue(true, true));
        assertTrue(multipleAreTrue(false, false, true, false, true));
    }

    @Test
    public void testSafeTrim() {
        assertEquals("", trim(null));
        assertEquals("", trim(""));
        assertEquals("hello", trim("hello"));
        assertEquals("hello", trim("  hello  "));
    }

    private static class FinalClass {
        private final String field = "immutable";
    }

    @Test
    public void settingFieldWithoutAccessThrowsException() throws NoSuchFieldException {
        final FinalClass o = new FinalClass();
        final Field field = FinalClass.class.getDeclaredField("field");

        assertThrows(AssignmentFailure.class, () ->
            Reflection.setField(field, o, "newValue"));
    }

}
