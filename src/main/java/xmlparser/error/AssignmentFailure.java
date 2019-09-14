package xmlparser.error;

public final class AssignmentFailure extends RuntimeException {
    public AssignmentFailure(final Throwable cause) {
        super("A field could not be assigned with an object, should not happen, ever.", cause);
    }
}
