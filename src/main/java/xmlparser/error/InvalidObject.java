package xmlparser.error;

public final class InvalidObject extends RuntimeException {
    public InvalidObject(final Class<?> clazz, final Exception cause) {
        super("Validator failed for an object of type " + clazz.getName(), cause);
    }
}
