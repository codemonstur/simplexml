package xmlparser.utils;

public enum Functions {;

    public static boolean isNullOrEmpty(final String value) {
        return value == null || value.isEmpty();
    }

    public static String trim(final String input) {
        return input == null ? "" : input.trim();
    }

}
