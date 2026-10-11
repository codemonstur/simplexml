package xmlparser.utils;

import static xmlparser.utils.Functions.isNullOrEmpty;

/**
 * Turns the text found in a document into values. What lives here has to behave the same
 * wherever the program runs and however many threads are reading at once, so nothing that is
 * locale dependent or keeps state between calls is allowed in.
 */
public enum Builder {;

    /**
     * Reads a number out of XML text. A '.' separates the decimal part and nothing else is a
     * separator, so the same text reads the same wherever the program runs, and the whole of the
     * text has to be the number: anything else is not a number and gives null. A NumberFormat
     * would be both locale dependent and unsafe to use from several threads at once.
     *
     * @param s the text to read a number out of, or null
     * @return the value as a Long when it is integral, as a Double when it has a decimal part,
     *         and null when the text is not a number
     */
    public static Number toNumber(final String s) {
        if (isNullOrEmpty(s)) return null;

        final String text = s.trim();

        // an integral value is kept whole so that a large one does not lose precision
        try {
            return Long.valueOf(text);
        } catch (final NumberFormatException ignore) { }

        try {
            final double value = Double.valueOf(text);
            return Double.isFinite(value) ? value : null;
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }

}
