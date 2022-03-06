package xmlparser.utils;

import xmlparser.error.InvalidXPath;

public enum Validator {;

    public static <T> T[] hasExactLength(final T[] array, final int size, final String message) throws InvalidXPath {
        if (array.length != size) throw new InvalidXPath(message);
        return array;
    }

    public static String[] partsAreNotEmpty(final String[] array, final String message) throws InvalidXPath {
        for (final String item : array) {
            if (item == null || item.isEmpty())
                throw new InvalidXPath(message);
        }
        return array;
    }

    public static boolean multipleAreTrue(final boolean... values) {
        int i = 0;
        for (; i < values.length; i++)
            if (values[i]) break;
        for (i++; i < values.length; i++)
            if (values[i]) return true;
        return false;
    }

    public static boolean multipleAreNotNull(final Object... values) {
        int i = 0;
        for (; i < values.length; i++)
            if (values[i] != null) break;
        for (i++; i < values.length; i++)
            if (values[i] != null) return true;
        return false;
    }

}
