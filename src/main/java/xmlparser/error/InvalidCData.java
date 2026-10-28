package xmlparser.error;

/**
 * Thrown when a text node marked as CDATA contains the illegal sequence {@code ]]>}.
 */
public final class InvalidCData extends RuntimeException {
    public InvalidCData(final String message) {
        super(message);
    }
}