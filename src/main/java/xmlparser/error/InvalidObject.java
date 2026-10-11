package xmlparser.error;

/**
 * Thrown when a method marked with {@link xmlparser.annotations.XmlObjectValidator} did not
 * accept an object that had just been read: the XML parsed all right, but what it describes is
 * not something the model is willing to hand back.
 */
public final class InvalidObject extends RuntimeException {
    /**
     * Reports a validator that would not accept the object it had just been handed.
     * @param clazz the type of the object the validator rejected
     * @param cause the failure the validator ran into
     */
    public InvalidObject(final Class<?> clazz, final Exception cause) {
        super("Validator failed for an object of type " + clazz.getName(), cause);
    }
}
