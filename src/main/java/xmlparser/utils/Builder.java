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

        // Double.valueOf takes a type suffix such as '5f', a hex float and a handful of other
        // spellings none of which is written in a document, so the text has to hold a plain
        // number before it is worth parsing
        if (!isPlainNumber(text)) return null;

        try {
            final double value = Double.valueOf(text);
            return Double.isFinite(value) ? value : null;
        } catch (final NumberFormatException ignore) {
            return null;
        }
    }

    // Tells whether the text is a plain number and nothing else: a sign, digits with at most one
    // '.' between them, and an exponent that is an 'e' followed by digits and at most one sign.
    // A leading '+' is allowed by Long.valueOf and Double.valueOf alike, and a '.' is allowed to
    // stand where no digit does on either side of it.
    private static boolean isPlainNumber(final String text) {
        int i = 0;
        if (text.startsWith("+") || text.startsWith("-")) i++;

        boolean digits = false;
        while (i < text.length() && isAsciiDigit(text.charAt(i))) { i++; digits = true; }

        if (i < text.length() && text.charAt(i) == '.') {
            i++;
            while (i < text.length() && isAsciiDigit(text.charAt(i))) { i++; digits = true; }
        }
        if (!digits) return false;

        if (i < text.length() && (text.charAt(i) == 'e' || text.charAt(i) == 'E')) {
            i++;
            if (i < text.length() && (text.charAt(i) == '+' || text.charAt(i) == '-')) i++;
            boolean exponent = false;
            while (i < text.length() && isAsciiDigit(text.charAt(i))) { i++; exponent = true; }
            if (!exponent) return false;
        }

        return i == text.length();
    }

    private static boolean isAsciiDigit(final char c) {
        return c >= '0' && c <= '9';
    }

}
