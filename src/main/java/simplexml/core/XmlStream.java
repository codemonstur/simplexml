package simplexml.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class XmlStream {
    private final InputStreamReader stream;

    public XmlStream(final InputStream stream) {
        this.stream = new InputStreamReader(stream, UTF_8);
    }

    public String readLine(final char end) throws IOException {
        final List<Character> chars = new LinkedList<>();
        int data;
        while ((data = stream.read()) != -1) {
            if (data == end) break;
            chars.add((char) data);
        }
        if (data == -1) return null;

        char[] value = new char[chars.size()];
        int i = 0;
        for (final Character c : chars) value[i++] = c;
        return new String(value);
    }
}
