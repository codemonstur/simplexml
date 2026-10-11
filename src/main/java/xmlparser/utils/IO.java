package xmlparser.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Opens the streams the parser reads from, so that every reader is buffered and decoded with the
 * charset the parser was built with.
 */
public enum IO {;

    /**
     * Wraps a stream in a buffered reader that decodes with the given charset.
     * @param in the stream to read from
     * @param charset the charset the text is stored in
     * @return a buffered reader over the stream
     */
    public static InputStreamReader newStreamReader(final InputStream in, final Charset charset) {
        return new InputStreamReader(new BufferedInputStream(in), charset);
    }
    /**
     * Turns a document that is already in memory into bytes in the given charset and hands them
     * back through a buffered reader.
     * @param xml the document text
     * @param charset the charset to store the text in
     * @return a buffered reader over the text
     */
    public static InputStreamReader newStreamReader(final String xml, final Charset charset) {
        return new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(xml.getBytes(charset))), charset);
    }

}
