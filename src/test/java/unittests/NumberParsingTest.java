package unittests;

import org.junit.Test;
import xmlparser.utils.Builder;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NumberParsingTest {

    @Test
    public void integralTextIsReadWhole() {
        assertEquals("An integral value lost its type", Long.valueOf(123), Builder.toNumber("123"));
        assertEquals("A negative integral value lost its type", Long.valueOf(-7), Builder.toNumber("-7"));
        assertEquals("A large integral value lost precision",
                Long.valueOf(9007199254740993L), Builder.toNumber("9007199254740993"));
    }

    @Test
    public void decimalTextIsReadAsADouble() {
        assertEquals("A decimal value was not read", 1.5, Builder.toNumber("1.5").doubleValue(), 0.0);
        assertEquals("A negative decimal value was not read", -3.25, Builder.toNumber("-3.25").doubleValue(), 0.0);
        assertEquals("A leading decimal point was not read", 0.5, Builder.toNumber(".5").doubleValue(), 0.0);
    }

    @Test
    public void aCommaIsNeverASeparator() {
        assertNull("A comma was swallowed as a grouping separator", Builder.toNumber("1,5"));
        assertNull("A comma was swallowed as a grouping separator", Builder.toNumber("1,500"));
        assertNull("A space was swallowed as a grouping separator", Builder.toNumber("1 500"));
    }

    @Test
    public void theWholeTextHasToBeTheNumber() {
        assertNull("Trailing text was ignored", Builder.toNumber("12abc"));
        assertNull("Trailing text was ignored", Builder.toNumber("1.5 items"));
    }

    @Test
    public void scientificNotationIsRead() {
        assertEquals("Scientific notation was not read", 100000.0, Builder.toNumber("1e5").doubleValue(), 0.0);
    }

    @Test
    public void aLeadingPlusIsRead() {
        assertEquals("A leading plus was not read", Long.valueOf(5), Builder.toNumber("+5"));
    }

    @Test
    public void surroundingWhitespaceIsIgnored() {
        assertEquals("Surrounding whitespace was not ignored", 1.5, Builder.toNumber(" 1.5").doubleValue(), 0.0);
        assertEquals("Surrounding whitespace was not ignored", Long.valueOf(4), Builder.toNumber("  4  "));
    }

    @Test
    public void textThatIsNotANumber() {
        assertNull("Text was read as a number", Builder.toNumber("abc"));
        assertNull("An empty string was read as a number", Builder.toNumber(""));
        assertNull("No text at all was read as a number", Builder.toNumber(null));
        assertNull("A non-finite value was read as a number", Builder.toNumber("Infinity"));
        assertNull("A non-finite value was read as a number", Builder.toNumber("NaN"));
    }

    @Test
    public void theSameTextReadsTheSameInEveryLocale() {
        final var original = Locale.getDefault();
        try {
            for (final Locale locale : new Locale[]{Locale.US, Locale.GERMANY, Locale.FRANCE, Locale.JAPAN, Locale.ROOT}) {
                Locale.setDefault(locale);

                assertEquals("The result depended on the default locale", 1.5, Builder.toNumber("1.5").doubleValue(), 0.0);
                assertEquals("The result depended on the default locale", Long.valueOf(123), Builder.toNumber("123"));
                assertEquals("The result depended on the default locale", -3.25, Builder.toNumber("-3.25").doubleValue(), 0.0);
                assertNull("The result depended on the default locale", Builder.toNumber("1,5"));
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

        assertEquals("Concurrent use gave a wrong result", 0, failures.get());
    }

}
