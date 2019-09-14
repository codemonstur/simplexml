package xmlparser.utils;

public enum Constants {;

    public static final char
        CHAR_FORWARD_SLASH = '/',
        CHAR_SPACE = ' ',
        CHAR_EQUALS = '=',
        CHAR_LESS_THAN = '<',
        CHAR_GREATER_THAN = '>',
        CHAR_QUESTION_MARK = '?',
        CHAR_SINGLE_QUOTE = '\'',
        CHAR_DOUBLE_QUOTE = '"',
        CHAR_AMPERSAND = '&';

    public static final char
        XML_TAG_START = CHAR_LESS_THAN,
        XML_TAG_END = CHAR_GREATER_THAN,
        XML_SELF_CLOSING = CHAR_FORWARD_SLASH,
        XML_PROLOG = CHAR_QUESTION_MARK;

    public static final String
        EMPTY = "",
        SPACE = " ",
        INDENT = "  ",
        LESS_THAN = "<",
        GREATER_THAN = ">",
        AMPERSAND = "&",
        EQUALS = "=",
        HASH = "#",
        SEMICOLON = ";",
        DOUBLE_QUOTE = "\"",
        FORWARD_SLASH = "/",
        NEW_LINE = "\n";

    public static final String
        ENCODED_LESS_THAN = "&lt;",
        ENCODED_GREATER_THAN = "&gt;",
        ENCODED_SINGLE_QUOTE = "&apos;",
        ENCODED_DOUBLE_QUOTE = "&quot;",
        ENCODED_AMPERSAND = "&amp;",
        ENCODED_UTF8 = "&#";

    public static final String
        PREDICATE_START_SYMBOL = "[",
        PREDICATE_END_SYMBOL = "]",
        PREDICATE_EQUAL_SYMBOL = "=",
        SEGMENT_EXPRESSION = "text()",
        EXPRESSION_PATH_SEPARATOR = "/";

    public static final String
        ERROR_EQUALITY_WITHOUT_TWO_COMPONENTS = "Equality predicate must have exactly two members",
        ERROR_EQUALITY_WITH_EMPTY_PARTS = "Equality predicate must have two non-empty members",
        ERROR_PREDICATE_WRONG_START = "Predicate does not start with [",
        ERROR_PREDICATE_WRONG_END = "Predicate does not end with ]",
        ERROR_PREDICATE_WRONG_NAME = "Element name contains ]";
}
