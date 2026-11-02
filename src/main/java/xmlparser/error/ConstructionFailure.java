package xmlparser.error;

/**
 * Reports a class that could not be instantiated through its no-args constructor.
 */
public final class ConstructionFailure extends RuntimeException {

    /**
     * Reports a class that could not be instantiated through its no-args constructor.
     * @param cause the failure that stopped the construction
     */
    public ConstructionFailure(final Class<?> clazz, final Throwable cause) {
        super("Class " + clazz.getName() + " could not be instantiated", cause);
    }

}
