package simplexml.model;

import java.io.IOException;

public final class InvalidXml extends IOException {
    public InvalidXml() {}
    public InvalidXml(final String message) {
        super(message);
    }
}
