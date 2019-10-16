package unittests;

import org.junit.Test;
import xmlparser.utils.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FunctionTest {

    @Test
    public void testMultipleAreTrue() {
        assertFalse(Validator.multipleAreTrue(false));
        assertFalse(Validator.multipleAreTrue(false, false, false, false));
        assertFalse(Validator.multipleAreTrue(false, false, true, false));
        assertFalse(Validator.multipleAreTrue(false, false, false, true));
        assertTrue("", Validator.multipleAreTrue(true, true));
        assertTrue("", Validator.multipleAreTrue(false, false, true, false, true));
    }

}
