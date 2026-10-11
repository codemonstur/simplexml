package xmlparser.error;

/**
 * Thrown when the input is not well formed XML: tags that do not balance, more than one root
 * element, content after the root has closed, a tag or a CDATA section that is never finished,
 * or a name that XML would not allow.
 */
public final class InvalidXml extends RuntimeException {
    /**
     * Reports input that is not well formed, with nothing further to say about it.
     */
    public InvalidXml() {}
    /**
     * Reports input that is not well formed, saying what is wrong with it.
     * @param message what is wrong with the input
     */
    public InvalidXml(final String message) {
        super(message);
    }
    /**
     * The same, for a failure that came out of another exception.
     * @param message what is wrong with the input
     * @param cause the exception that led here
     */
    public InvalidXml(final String message, final Throwable cause) {
        super(message, cause);
    }
}
