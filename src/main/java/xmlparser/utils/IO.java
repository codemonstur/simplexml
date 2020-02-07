package xmlparser.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public enum IO {;

    public static InputStreamReader newStreamReader(final String xml, final Charset charset) {
        return new InputStreamReader(new ByteArrayInputStream(xml.getBytes(charset)), charset);
    }

}
