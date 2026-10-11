package xmlparser.error;

/**
 * Thrown when an annotation asks for something that cannot be done: a type missing from an
 * {@link xmlparser.annotations.XmlAbstractClass} type map, a field deserializer that does not
 * exist, a collection type with no constructor of its own. The XML may well be fine, the model
 * is what is wrong.
 */
public final class InvalidAnnotation extends RuntimeException {
    /**
     * Reports an annotation asking for something that cannot be done.
     * @param message what is wrong with the model
     */
    public InvalidAnnotation(final String message) {
        super(message);
    }
    /**
     * The same, for a failure that came out of another exception.
     * @param message what is wrong with the model
     * @param cause the exception that led here
     */
    public InvalidAnnotation(final String message, final Throwable cause) {
        super(message, cause);
    }
}
