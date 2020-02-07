package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.utils.Interfaces.CheckedIterator;
import xmlparser.utils.Trimming.Trim;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static xmlparser.utils.IO.newStreamReader;

public interface XmlIterator {

    default CheckedIterator<String> iterateXml(final InputStreamReader in) {
        return new CheckedIterator<String>() {
            public boolean hasNext() throws IOException {
                final Character next = readFirstNonWhiteChar(in);
                if (next == null) return false;
                if (next == '<') return true;
                throw new InvalidXml();
            }

            public String next() throws IOException {
                return readUntilCurrentTagIsClosed(in);
            }
        };
    }

    default CheckedIterator<XmlElement> iterateDom(final InputStreamReader in, final Charset charset, final Trim trimmer) {
        return new CheckedIterator<XmlElement>() {
            public boolean hasNext() throws Exception {
                final Character next = readFirstNonWhiteChar(in);
                if (next == null) return false;
                if (next == '<') return true;
                throw new InvalidXml();
            }

            public XmlElement next() throws Exception {
                final String xml = readUntilCurrentTagIsClosed(in);
                try (final InputStreamReader in = newStreamReader(xml, charset)) {
                    return XmlReader.toXmlDom(in, trimmer);
                }
            }
        };
    }

    default <T> CheckedIterator<T> iterateObject(final InputStreamReader in, final Charset charset, final XmlReader reader
            , final Class<T> clazz, final Trim trimmer) {
        return new CheckedIterator<T>() {
            public boolean hasNext() throws Exception {
                final Character next = readFirstNonWhiteChar(in);
                if (next == null) return false;
                if (next == '<') return true;
                throw new InvalidXml();
            }

            public T next() throws Exception {
                final String xml = readUntilCurrentTagIsClosed(in);
                try (final InputStreamReader in = newStreamReader(xml, charset)) {
                    final XmlElement element = XmlReader.toXmlDom(in, trimmer);
                    return reader.domToObject(element, clazz);
                }
            }
        };
    }

    static Character readFirstNonWhiteChar(final InputStreamReader in) throws IOException {
        int r;
        while ((r = in.read()) != -1) {
            final char c = (char) r;
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') continue;
            return c;
        }
        return null;
    }

    static String readUntilCurrentTagIsClosed(final InputStreamReader in) throws IOException {
        final StringBuilder builder = new StringBuilder();
        builder.append('<');

        boolean previousWasForwardSlash = false;
        boolean previousWasSmallerThan = true;

        int numberOfTagsOpened = 1;

        int r;
        while ((r = in.read()) != -1) {
            final char c = (char) r;
            builder.append(c);
            if (c == '>' && previousWasForwardSlash) numberOfTagsOpened--;
            if (c == '/' && previousWasSmallerThan) numberOfTagsOpened -= 2;
            if (c == '<') numberOfTagsOpened++;

            if (numberOfTagsOpened < 0) throw new InvalidXml();
            if (numberOfTagsOpened == 0) {
                if (c != '>') {
                    while ((r = in.read()) != -1) {
                        final char ch = (char) r;
                        builder.append(ch);
                        if (ch == '>') break;
                    }
                }
                break;
            }

            previousWasForwardSlash = c == '/';
            previousWasSmallerThan = c == '<';
        }
        return builder.toString();
    }

}
