package xmlparser.error;

/**
 * Thrown when a text node marked as CDATA contains the illegal sequence {@code ]]>}.
 */
public final class InvalidCData extends RuntimeException {

    /**
     * Creates an exception for CDATA content that contains the illegal terminator.
     * @param message the detail message
     */
    public InvalidCData(final String message) {
        super(message);
    }
}