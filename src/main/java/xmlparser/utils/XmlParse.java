package xmlparser.utils;

import xmlparser.error.InvalidXml;

import java.io.IOException;
import java.io.Reader;

import static xmlparser.utils.Constants.CHAR_DOUBLE_QUOTE;
import static xmlparser.utils.Constants.CHAR_EXCLAMATION_MARK;
import static xmlparser.utils.Constants.CHAR_FORWARD_SLASH;
import static xmlparser.utils.Constants.CHAR_SINGLE_QUOTE;
import static xmlparser.utils.Constants.GREATER_THAN;
import static xmlparser.utils.Constants.XML_END_CDATA;
import static xmlparser.utils.Constants.XML_END_COMMENT;
import static xmlparser.utils.Constants.XML_PROLOG;
import static xmlparser.utils.Constants.XML_START_CDATA;
import static xmlparser.utils.Constants.XML_START_COMMENT;
import static xmlparser.utils.Constants.XML_TAG_END;
import static xmlparser.utils.Trimming.Trim;

/**
 * Reads the low level pieces of a document back off a stream: everything up to the next tag, a
 * tag with whatever it holds, a CDATA section, and the name at the front of a tag. Everything
 * above this works on the strings these methods hand back.
 */
public enum XmlParse {;

    /**
     * Takes the name off the front of a tag, which runs up to the first character the trimmer
     * counts as whitespace or up to the '/' of a self closing tag, whichever comes first.
     *
     * @param tag the tag text, without the opening '&lt;'
     * @param trimmer the opinion of whitespace that ends the name
     * @return the name, empty when the tag starts with whitespace or a '/'
     */
    public static String getNameOfTag(final String tag, final Trim trimmer) {
        int offset = 0;
        for (; offset < tag.length(); offset++) {
            if (trimmer.isWhitespace(tag.charAt(offset)) || tag.charAt(offset) == CHAR_FORWARD_SLASH)
                break;
        }
        return tag.substring(0, offset);
    }

    /**
     * Reads characters up to the first one equal to end, which is not kept. Running out of input
     * instead gives null, unless end is '&lt;' and untagged text was left over, which is an error
     * rather than something to return.
     *
     * @param in the stream to read from
     * @param trimmer the opinion of whitespace that decides whether anything was left over
     * @param end the character to stop at, which is not itself kept
     * @return everything read before end, or null when the input ran out first
     * @throws IOException if the stream cannot be read
     * @throws InvalidXml if the input ran out with untagged text still to read
     */
    public static String readLine(final Reader in, final Trim trimmer, final char end) throws IOException {
        final StringBuilder chars = new StringBuilder();
        int data;
        while ((data = in.read()) != -1) {
            if (data == end) break;
            chars.append((char)data);
        }
        if (data == -1) {
            if (end == '<') {
                final var rest = trimmer.trim(chars.toString());
                if (!rest.isEmpty()) throw new InvalidXml("Found remaining text after last tag: " + rest);
            }
            return null;
        }
        return chars.toString();
    }

    /**
     * Reads the body of a CDATA section once its opening line has been read up to a '&gt;'.
     * That '&gt;' can sit inside the section instead of closing it, so it is put back before
     * searching for the terminator that really ends the section.
     *
     * @param in   the stream, positioned just after the first '&gt;' of the section
     * @param head everything read since the '&lt;' of the section, starting with {@code ![CDATA[}
     * @return the verbatim text between the markers, without the markers themselves
     * @throws IOException if the stream cannot be read
     * @throws InvalidXml if the section is never terminated
     */
    public static String readCData(final Reader in, final String head) throws IOException {
        final String terminator = XML_END_CDATA + GREATER_THAN;
        String section = head + GREATER_THAN;

        // the '>' that ended the read either completed the terminator or was part of the body
        if (!section.endsWith(terminator)) {
            final String rest = readUntil(in, terminator);
            if (rest == null) throw new InvalidXml("Unterminated CDATA section");
            section += rest;
        }

        return section.substring(XML_START_CDATA.length(), section.length() - terminator.length());
    }

    /**
     * Reads one tag, which ends at the first '&gt;' that is not inside a quoted attribute value
     * because an attribute value may contain one. A prolog or processing instruction ends at the
     * '&gt;' of its '?&gt;' instead, and that can sit inside a quoted value of its data, so it is
     * read to whichever of the two comes first. A comment or a section ends at a marker of its own
     * and its content may hold a quote that is never closed, so it is read to the plain next
     * '&gt;' and finished off by the caller.
     *
     * @param in the stream, positioned just after the '&lt;' that opens the tag
     * @return everything read since the '&lt;', or null when the input runs out
     * @throws IOException if the stream cannot be read
     * @throws InvalidXml if the input runs out with a quoted value still open
     */
    public static String readTag(final Reader in) throws IOException {
        int data = in.read();
        if (data == -1) return null;
        final boolean isCommentOrSection = data == CHAR_EXCLAMATION_MARK;
        final boolean isProlog = data == XML_PROLOG;

        final StringBuilder chars = new StringBuilder();
        char quote = 0;
        while (data != -1) {
            final char c = (char) data;
            if (c == XML_TAG_END && endsTag(chars, isCommentOrSection, isProlog, quote))
                return chars.toString();

            if (!isCommentOrSection && (c == CHAR_DOUBLE_QUOTE || c == CHAR_SINGLE_QUOTE))
                quote = quote == 0 ? c : (quote == c ? 0 : quote);
            chars.append(c);
            data = in.read();
        }
        if (quote != 0) throw new InvalidXml("Unterminated quoted value before the end of the input");
        return null;
    }

    private static boolean endsTag(final StringBuilder chars, final boolean isCommentOrSection,
                                   final boolean isProlog, final char quote) {
        if (isCommentOrSection) return true;
        if (!isProlog) return quote == 0;
        return quote == 0 || endsWithProlog(chars);
    }

    private static boolean endsWithProlog(final StringBuilder chars) {
        return !chars.isEmpty() && chars.charAt(chars.length()-1) == XML_PROLOG;
    }

    /**
     * Reads until what has been read so far ends with end, which is kept as part of the result.
     *
     * @param in the stream to read from
     * @param end the marker that ends the read, kept with everything read before it
     * @return everything read including end, or null when the input runs out before it appears
     * @throws IOException if the stream cannot be read
     */
    public static String readUntil(final Reader in, final String end) throws IOException {
        final StringBuilder chars = new StringBuilder();

        // read the first few bytes that can't match yet
        for (int i = 0; i < end.length(); i++) {
            final int data = in.read();
            if (data == -1) return null;
            chars.append((char) data);
        }

        if (isEndReached(chars, end)) return chars.toString();

        int data;
        while ((data = in.read()) != -1) {
            chars.append((char)data);
            if (isEndReached(chars, end)) return chars.toString();
        }
        return null;
    }

    private static boolean isEndReached(final StringBuilder chars, final String postfix) {
        final int start = chars.length() - postfix.length();
        final int end = chars.length();
        return chars.substring(start, end).equals(postfix);
    }

    /**
     * Reads whatever carries on after a comment or a declaration whose first read ended at a
     * '&gt;' that does not really close it: a comment runs to its '--&gt;', and a declaration
     * that has opened an internal subset to the ']' after that subset. The '&gt;' which ended
     * the first read belongs to the tag either way and has been consumed, so the caller puts it
     * back between the head and whatever comes back here. A tag that was already read whole has
     * nothing left to read.
     *
     * <p>A prolog is not finished here: it may be written with a bare '&gt;' rather than the
     * '?&gt;' it is supposed to end with, and reading on for one that never appears would take
     * the rest of the document with it. Its caller decides which of the two it was by looking
     * for the '?&gt;' in the run of text that follows.</p>
     *
     * @param in the stream to read from, positioned just after the '&gt;' that ended the first read
     * @param head everything read since the '&lt;' of the tag
     * @return what carries on after that '&gt;', or null when the tag is already read whole
     * @throws IOException if the stream cannot be read
     */
    public static String finishTag(final Reader in, final String head) throws IOException {
        if (head.startsWith(XML_START_COMMENT))
            return head.endsWith(XML_END_COMMENT) ? null : readUntil(in, XML_END_COMMENT + GREATER_THAN);

        if (head.startsWith(String.valueOf(CHAR_EXCLAMATION_MARK)) && hasOpenSubset(head))
            return readUntil(in, "]" + GREATER_THAN);

        return null;
    }

    /**
     * Tells whether a declaration has opened an internal subset, as a DOCTYPE does with the
     * '[...]' that follows it, without one closing it again. A bracket inside a quoted value
     * belongs to that value and opens nothing, so quotes are tracked while looking.
     *
     * @param head everything read since the '&lt;' of the declaration
     * @return true when the subset is still open where the head ends
     */
    public static boolean hasOpenSubset(final String head) {
        char quote = 0;
        boolean isOpen = false;
        for (int i = 0; i < head.length(); i++) {
            final char c = head.charAt(i);
            if (c == CHAR_SINGLE_QUOTE || c == CHAR_DOUBLE_QUOTE)
                quote = quote == 0 ? c : (quote == c ? 0 : quote);
            else if (quote == 0 && c == '[') isOpen = true;
            else if (quote == 0 && c == ']') isOpen = false;
        }
        return isOpen;
    }

    /**
     * Finds the first character at or after offset that the trimmer does not count as whitespace.
     *
     * @param input the text to look through
     * @param offset the index to start at
     * @param trimmer the opinion of whitespace to test with
     * @return the index of the character, or -1 when there is none
     */
    public static int indexOfNonWhitespaceChar(final String input, final int offset, final Trim trimmer) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (trimmer.isWhitespace(at)) continue;
            return i;
        }
        return -1;
    }

    /**
     * Finds the first character at or after offset that the trimmer counts as whitespace.
     *
     * @param input the text to look through
     * @param offset the index to start at
     * @param trimmer the opinion of whitespace to test with
     * @return the index of the character, or -1 when there is none
     */
    public static int indexOfWhitespaceChar(final String input, final int offset, final Trim trimmer) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (trimmer.isWhitespace(at)) return i;
        }
        return -1;
    }

}
