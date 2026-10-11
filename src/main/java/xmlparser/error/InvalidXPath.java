package xmlparser.error;

/**
 * Thrown when an XPath expression cannot be parsed: an empty segment, such as the one a double
 * separator or a trailing separator leaves behind, a predicate that does not open or close where
 * it should, or an expression that is not there at all.
 */
public final class InvalidXPath extends RuntimeException {
    /**
     * Reports an XPath expression that cannot be parsed.
     * @param message what is wrong with the expression
     */
    public InvalidXPath(final String message) {
        super(message);
    }
}
