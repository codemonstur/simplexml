package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.utils.Builder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NumberParsingTest {

    @Test
    public void integralTextIsReadWhole() {
        assertEquals(Long.valueOf(123), Builder.toNumber("123"), "An integral value lost its type");
        assertEquals(Long.valueOf(-7), Builder.toNumber("-7"), "A negative integral value lost its type");
        assertEquals(Long.valueOf(9007199254740993L), Builder.toNumber("9007199254740993"), "A large integral value lost precision");
    }

    @Test
    public void decimalTextIsReadAsADouble() {
        assertEquals(1.5, Builder.toNumber("1.5").doubleValue(), 0.0, "A decimal value was not read");
        assertEquals(-3.25, Builder.toNumber("-3.25").doubleValue(), 0.0, "A negative decimal value was not read");
        assertEquals(0.5, Builder.toNumber(".5").doubleValue(), 0.0, "A leading decimal point was not read");
    }

    @Test
    public void aCommaIsNeverASeparator() {
        assertNull(Builder.toNumber("1,5"), "A comma was swallowed as a grouping separator");
        assertNull(Builder.toNumber("1,500"), "A comma was swallowed as a grouping separator");
        assertNull(Builder.toNumber("1 500"), "A space was swallowed as a grouping separator");
    }

    @Test
    public void theWholeTextHasToBeTheNumber() {
        assertNull(Builder.toNumber("12abc"), "Trailing text was ignored");
        assertNull(Builder.toNumber("1.5 items"), "Trailing text was ignored");
    }

    @Test
    public void scientificNotationIsRead() {
        assertEquals(100000.0, Builder.toNumber("1e5").doubleValue(), 0.0, "Scientific notation was not read");
    }

    @Test
    public void aLeadingPlusIsRead() {
        assertEquals(Long.valueOf(5), Builder.toNumber("+5"), "A leading plus was not read");
    }

    @Test
    public void surroundingWhitespaceIsIgnored() {
        assertEquals(1.5, Builder.toNumber(" 1.5").doubleValue(), 0.0, "Surrounding whitespace was not ignored");
        assertEquals(Long.valueOf(4), Builder.toNumber("  4  "), "Surrounding whitespace was not ignored");
    }

    @Test
    public void textThatIsNotANumber() {
        assertNull(Builder.toNumber("abc"), "Text was read as a number");
        assertNull(Builder.toNumber(""), "An empty string was read as a number");
        assertNull(Builder.toNumber(null), "No text at all was read as a number");
        assertNull(Builder.toNumber("Infinity"), "A non-finite value was read as a number");
        assertNull(Builder.toNumber("NaN"), "A non-finite value was read as a number");
    }

    @Test
    public void theSameTextReadsTheSameInEveryLocale() {
        final var original = Locale.getDefault();
        try {
            for (final Locale locale : new Locale[]{Locale.US, Locale.GERMANY, Locale.FRANCE, Locale.JAPAN, Locale.ROOT}) {
                Locale.setDefault(locale);

                assertEquals(1.5, Builder.toNumber("1.5").doubleValue(), 0.0, "The result depended on the default locale");
                assertEquals(Long.valueOf(123), Builder.toNumber("123"), "The result depended on the default locale");
                assertEquals(-3.25, Builder.toNumber("-3.25").doubleValue(), 0.0, "The result depended on the default locale");
                assertNull(Builder.toNumber("1,5"), "The result depended on the default locale");
            }
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void aSharedFormatterIsSafeToUseFromSeveralThreads() throws Exception {
        final var threads = 8;
        final var each = 20_000;
        final var pool = java.util.concurrent.Executors.newFixedThreadPool(threads);
        final var failures = new java.util.concurrent.atomic.AtomicInteger();
        final java.util.List<java.util.concurrent.Future<?>> tasks = new java.util.ArrayList<>();

        for (int t = 0; t < threads; t++) tasks.add(pool.submit(() -> {
            for (int i = 0; i < each; i++) {
                if (Builder.toNumber("1.5") == null || Builder.toNumber("1.5").doubleValue() != 1.5) failures.incrementAndGet();
                if (!Long.valueOf(123).equals(Builder.toNumber("123"))) failures.incrementAndGet();
                if (Builder.toNumber("1,5") != null) failures.incrementAndGet();
            }
        }));
        for (final var task : tasks) task.get();
        pool.shutdown();

        assertEquals(0, failures.get(), "Concurrent use gave a wrong result");
    }

}
