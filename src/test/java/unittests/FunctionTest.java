package unittests;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

}
