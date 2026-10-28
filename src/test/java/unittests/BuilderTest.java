package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.utils.Builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BuilderTest {

    @Test
    public void toNumberNullAndEmpty() {
        assertNull(Builder.toNumber(null));
        assertNull(Builder.toNumber(""));
        assertNull(Builder.toNumber("   "));
    }

    @Test
    public void toNumberIntegral() {
        assertEquals(42L, Builder.toNumber("42"));
        assertEquals(-7L, Builder.toNumber("-7"));
        assertEquals(5L, Builder.toNumber("+5"));
    }

    @Test
    public void toNumberDecimal() {
        assertEquals(3.5, Builder.toNumber("3.5"));
        assertEquals(.5, Builder.toNumber(".5"));
        assertEquals(5., Builder.toNumber("5."));
    }

    @Test
    public void toNumberExponent() {
        assertEquals(1.0e3, Builder.toNumber("1e3"));
        assertEquals(2.5E-2, Builder.toNumber("2.5E-2"));
        assertEquals(4.0, Builder.toNumber("+4e+0"));
    }

    @Test
    public void toNumberRejectsNonPlain() {
        assertNull(Builder.toNumber("5f"));
        assertNull(Builder.toNumber("0x10"));
        assertNull(Builder.toNumber("1.2.3"));
        assertNull(Builder.toNumber("1e"));
        assertNull(Builder.toNumber("e3"));
        assertNull(Builder.toNumber("abc"));
        assertNull(Builder.toNumber("1_000"));
    }

    @Test
    public void toNumberRejectsInfinityAndNaN() {
        assertNull(Builder.toNumber("Infinity"));
        assertNull(Builder.toNumber("NaN"));
    }
}
