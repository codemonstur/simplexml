package simplexml.utils;

import simplexml.error.InvalidXPath;

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

}
