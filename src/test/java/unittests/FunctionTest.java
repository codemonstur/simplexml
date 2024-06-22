package unittests;

import org.junit.Test;
import xmlparser.error.AssignmentFailure;
import xmlparser.utils.Reflection;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.objenesis.ObjenesisHelper.newInstance;
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

    @Test(expected = AssignmentFailure.class)
    public void settingFieldWithoutAccessThrowsException() throws NoSuchFieldException {
        final FinalClass o = newInstance(FinalClass.class);
        final Field field = FinalClass.class.getDeclaredField("field");

        Reflection.setField(field, o, "newValue");
    }

//    @Test
//    public void invalidCallToCanonicalConstructorOfRecord() {
//        Reflection.canonicalConstructorOfRecord(FunctionTest.class);
//    }

}
