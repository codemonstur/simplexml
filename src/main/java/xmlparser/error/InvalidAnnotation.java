package xmlparser.error;

public final class InvalidAnnotation extends RuntimeException {
    public InvalidAnnotation(final String message) {
        super(message);
    }
    public InvalidAnnotation(final String message, final Throwable cause) {
        super(message, cause);
    }
}
