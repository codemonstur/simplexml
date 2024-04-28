package xmlparser.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public enum IO {;

    public static InputStreamReader newStreamReader(final InputStream in, final Charset charset) {
        return new InputStreamReader(new BufferedInputStream(in), charset);
    }
    public static InputStreamReader newStreamReader(final String xml, final Charset charset) {
        return new InputStreamReader(new BufferedInputStream(new ByteArrayInputStream(xml.getBytes(charset))), charset);
    }

}
