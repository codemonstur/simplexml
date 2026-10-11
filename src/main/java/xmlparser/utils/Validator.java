package xmlparser.utils;

import xmlparser.error.InvalidXPath;

/**
 * Checks that hold before anything is built, and that say what went wrong when they do not. Used
 * mostly while parsing XPath expressions, where a bad shape has to be reported as an error rather
 * than turned into a path that quietly matches nothing.
 */
public enum Validator {;

    /**
     * Checks that an array holds exactly as many parts as were expected, and hands it back so the
     * check can wrap an expression inline.
     * @param <T> the type of the array's elements
     * @param array the array to check
     * @param size the number of parts expected
     * @param message the error to report when the length is different
     * @return the array that was passed in
     * @throws InvalidXPath if the array's length differs from size
     */
    public static <T> T[] hasExactLength(final T[] array, final int size, final String message) throws InvalidXPath {
        if (array.length != size) throw new InvalidXPath(message);
        return array;
    }

    /**
     * Checks that every part of an array is present, and hands the array back so the check can
     * wrap an expression inline.
     * @param array the parts to check
     * @param message the error to report when a part is missing
     * @return the array that was passed in
     * @throws InvalidXPath if any part is null or empty
     */
    public static String[] partsAreNotEmpty(final String[] array, final String message) throws InvalidXPath {
        for (final String item : array) {
            if (item == null || item.isEmpty())
                throw new InvalidXPath(message);
        }
        return array;
    }

    /**
     * Tells whether more than one of the values is true; a single true among them, or none, is
     * not multiple.
     * @param values the flags to look at
     * @return true only when two or more values are true
     */
    public static boolean multipleAreTrue(final boolean... values) {
        int i = 0;
        for (; i < values.length; i++)
            if (values[i]) break;
        for (i++; i < values.length; i++)
            if (values[i]) return true;
        return false;
    }

    /**
     * Tells whether more than one of the values is non-null; a single one among them, or none, is
     * not multiple.
     * @param values the values to look at
     * @return true only when two or more values are non-null
     */
    public static boolean multipleAreNotNull(final Object... values) {
        int i = 0;
        for (; i < values.length; i++)
            if (values[i] != null) break;
        for (i++; i < values.length; i++)
            if (values[i] != null) return true;
        return false;
    }

}
