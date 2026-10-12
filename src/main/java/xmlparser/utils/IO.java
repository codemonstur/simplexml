package xmlparser.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
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
     * Hands a document that is already in memory back through a reader. The text holds the
     * characters themselves rather than bytes waiting to be decoded, so it is read as it is:
     * storing it in the given charset and reading it back would replace every character that
     * charset cannot hold, which is not a thing the document meant.
     * @param xml the document text
     * @param charset the charset the text would have been stored in, which reading it back does
     *                not need: the text is characters rather than bytes
     * @return a reader over the text
     */
    public static Reader newStreamReader(final String xml, final Charset charset) {
        return new StringReader(xml);
    }

}
