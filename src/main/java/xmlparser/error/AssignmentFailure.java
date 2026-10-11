package xmlparser.error;

/**
 * Thrown when a field could not be given the value that had been read for it. Nothing ordinary
 * leads here: it exists so that whatever went wrong is reported instead of being dropped.
 */
public final class AssignmentFailure extends RuntimeException {
    /**
     * Reports a field that could not be given the value read for it.
     * @param cause the failure that stopped the assignment
     */
    public AssignmentFailure(final Throwable cause) {
        super("A field could not be assigned with an object, should not happen, ever.", cause);
    }
}
