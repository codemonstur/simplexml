package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.utils.Trimming.Trim;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static xmlparser.utils.Constants.*;
import static xmlparser.utils.IO.newStreamReader;
import static xmlparser.utils.XmlParse.*;

/**
 * Takes a document that was written to be read by a person and writes it back to be read by a
 * machine. The whitespace that pretty printing put between tags is dropped and text nodes are
 * trimmed, comments and prologs are left out entirely, and a CDATA section is copied through
 * untouched because its body is literal.
 */
public interface XmlCompress {

    /**
     * Compresses a document held in a string, read and written as UTF-8 whatever charset the
     * parser was built with.
     *
     * @param input the document to compress
     * @param trimmer the opinion of whitespace to trim text and attribute values with
     * @return the compressed document
     * @throws InvalidXml if the input cannot be read or holds a tag that is never closed
     */
    default String compressXml(final String input, final Trim trimmer) {
        try ( final var out = new ByteArrayOutputStream()
            ; final var reader = newStreamReader(input, UTF_8)
            ; final var writer = new OutputStreamWriter(out, UTF_8)
            ) {
            compressXML(reader, writer, trimmer);
            return out.toString(UTF_8);
        } catch (final IOException e) {
            throw new InvalidXml("Could not read the XML to compress it", e);
        }
    }

    /**
     * Reads the document from the reader and writes its compressed form to the writer: text nodes
     * and attribute values are trimmed, closing tags keep only their name, and comments and
     * prologs are read past and dropped. A CDATA section is copied through untouched, because its
     * body is literal. The writer is flushed once the input runs out.
     *
     * @param in the reader to read the document from
     * @param out the writer to write the compressed document to
     * @param trimmer the opinion of whitespace to trim with
     * @throws IOException if reading or writing fails
     * @throws InvalidXml if the input ends inside a tag
     */
    static void compressXML(final InputStreamReader in, final OutputStreamWriter out, final Trim trimmer) throws IOException {
        String str; while ((str = readLine(in, trimmer, XML_TAG_START)) != null) {
            // Probably a text node, remove the whitespace and write
            if (!str.isEmpty()) out.write(trimmer.trim(str));

            final String head = readTag(in);
            // A CDATA section is copied verbatim: its body is literal, so rewriting it would change it
            if (head != null && head.startsWith(XML_START_CDATA)) {
                out.write(XML_TAG_START);
                out.write(XML_START_CDATA);
                out.write(readCData(in, head));
                out.write(XML_END_CDATA);
                out.write(XML_TAG_END);
                continue;
            }
            str = trimmer.trim(head);
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            // We don't care about comments, but they can contain a '>' so skip to the end of the comment
            if (str.startsWith(XML_START_COMMENT)) {
                if (!str.endsWith(XML_END_COMMENT)) readUntil(in, XML_END_COMMENT + GREATER_THAN);
                continue;
            }
            // Prologs are dropped as well, and their attribute values can contain a '>' too
            if (str.charAt(0) == XML_PROLOG) {
                if (!str.endsWith(String.valueOf(XML_PROLOG)))
                    readUntil(in, String.valueOf(XML_PROLOG) + GREATER_THAN);
                continue;
            }

            out.write(XML_TAG_START);
            // It might be a closing tag which is easy to deal with
            if (str.charAt(0) == CHAR_FORWARD_SLASH) {
                out.write(CHAR_FORWARD_SLASH);
                out.write(getNameOfTag(str.substring(1), trimmer));
                out.write(XML_TAG_END);
                continue;
            }

            // Its an opening tag so we need the attributes and the whole thing
            final String name = getNameOfTag(str, trimmer);
            if (str.length() == name.length()) {
                out.write(name);
                out.write(XML_TAG_END);
                continue;
            }

            final int beginAttr = name.length();
            final int end = str.length();
            out.write(name);
            // It could be a self closing tag, in which case the '/' follows the name with nothing
            // between them and has to be cut off, while an opening tag has a space to skip
            if (str.endsWith(FORWARD_SLASH)) {
                parseAttributes(str.substring(beginAttr, end-1), out, trimmer);
                out.write(FORWARD_SLASH);
            } else {
                parseAttributes(str.substring(beginAttr+1, end), out, trimmer);
            }
            out.write(XML_TAG_END);
        }
        out.flush();
    }

    /**
     * Reads everything after the name of an opening tag and writes each attribute back as
     * name="value", the value trimmed and requoted with double quotes whether it was single
     * quoted, double quoted or unquoted. A name with no '=', or a value that never starts, ends
     * the list and whatever is left of the input is dropped.
     *
     * @param input the inside of the tag, after its name
     * @param out the writer to write the attributes to
     * @param trimmer the opinion of whitespace to trim names and values with
     * @throws IOException if writing fails
     */
    static void parseAttributes(String input, final OutputStreamWriter out, final Trim trimmer) throws IOException {

        while (!input.isEmpty()) {
            final int startName = indexOfNonWhitespaceChar(input, 0, trimmer);
            if (startName == -1) break;
            final int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) break;

            final String name = trimmer.trim(input.substring(startName, equals));
            input = input.substring(equals+1);

            int startValue = indexOfNonWhitespaceChar(input, 0, trimmer);
            if (startValue == -1) break;

            int endValue; final String value;
            final char quote = input.charAt(startValue);
            if (quote == CHAR_DOUBLE_QUOTE || quote == CHAR_SINGLE_QUOTE) {
                startValue++;
                endValue = input.indexOf(quote, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = trimmer.trim(input.substring(startValue, endValue));
            } else {
                endValue = indexOfWhitespaceChar(input, startValue+1, trimmer);
                if (endValue == -1) endValue = input.length()-1;
                value = trimmer.trim(input.substring(startValue, endValue+1));
            }

            input = input.substring(endValue+1);

            out.write(CHAR_SPACE);
            out.write(name);
            out.write(CHAR_EQUALS);
            out.write(CHAR_DOUBLE_QUOTE);
            out.write(value);
            out.write(CHAR_DOUBLE_QUOTE);
        }
    }

}
