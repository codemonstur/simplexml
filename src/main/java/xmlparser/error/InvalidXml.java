package xmlparser.error;

public final class InvalidXml extends RuntimeException {
    public InvalidXml() {}
    public InvalidXml(final String message) {
        super(message);
    }
}
