package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

public enum IO {;

    public static String resourceToString(final String resource) throws IOException {
        try (final InputStream in = IO.class.getResourceAsStream(resource)) {
            return toString(in);
        }
    }

    public static String toString(final InputStream in) throws IOException {
        return toString(in, UTF_8);
    }
    public static String toString(final InputStream in, final Charset set) throws IOException {
        final StringBuilder out = new StringBuilder();
        byte[] buffer = new byte[1024]; int read;
        while ((read = in.read(buffer)) != -1) {
            out.append(new String(buffer, 0, read, set));
        }
        return out.toString();
    }

}
