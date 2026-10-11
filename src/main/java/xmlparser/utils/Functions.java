package xmlparser.utils;

/**
 * The handful of helpers that are wanted in most files and are easier to name than to write out
 * each time.
 */
public enum Functions {;

    /**
     * Tells whether a string is null or has no characters in it.
     * @param value the string to look at, which may be null
     * @return true when the string is null or empty
     */
    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Trims a string, turning null into the empty string instead of failing.
     * @param input the string to trim, which may be null
     * @return the trimmed string, or an empty string when the input is null
     */
    public static String trim(final String input) {
        return input == null ? "" : input.trim();
    }

}
