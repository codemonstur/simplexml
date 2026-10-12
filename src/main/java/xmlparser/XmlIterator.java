package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.utils.CheckedIterator;
import xmlparser.utils.Escaping.UnEscape;
import xmlparser.utils.Trimming.Trim;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.IO.newStreamReader;
import static xmlparser.utils.XmlParse.finishTag;
import static xmlparser.utils.XmlParse.readCData;
import static xmlparser.utils.XmlParse.readTag;

/**
 * Hands a document back one top level element at a time instead of all at once. Nothing is read
 * ahead: a file holding thousands of items can be walked while only one of them is in memory,
 * and each item can be taken as XML, as a DOM element, or as an object.
 */
public interface XmlIterator {

    /**
     * Hands back each top level element of a document in turn, as the XML it was written as, with
     * nothing read ahead past the end of one. A comment, a prolog or a declaration sitting between
     * two elements belongs to neither of them and is read past, one sitting inside an element is
     * copied through as it is, and CDATA and self-closing tags are copied through verbatim.
     *
     * @param in the stream to read the elements from
     * @return an iterator over the elements, ending at the end of the stream
     */
    default CheckedIterator<String> iterateXml(final Reader in) {
        return new CheckedIterator<>() {
            private String pending;

            public boolean hasNext() throws IOException {
                if (pending != null) return true;
                final Character next = readFirstNonWhiteChar(in);
                if (next == null) return false;
                if (next != '<') throw new InvalidXml();
                // the item is read in full here so that hasNext() can be called twice, or next()
                // called without it, without reading past the item being looked at
                pending = readUntilCurrentTagIsClosed(in);
                return true;
            }

            public String next() throws IOException {
                if (!hasNext()) throw new NoSuchElementException("No more items to read");
                final String xml = pending;
                pending = null;
                return xml;
            }
        };
    }

    /**
     * Hands back each top level element of a document in turn as a DOM tree, treating comments,
     * prologs, CDATA and self-closing tags the way {@link #iterateXml} does, and unescaping and
     * trimming the text the way a whole document is normally read.
     *
     * @param in the stream to read the elements from
     * @param charset the charset the element text is decoded with before it is parsed
     * @param conserveWhitespace whether whitespace in text is kept instead of trimmed
     * @param trimmer what counts as whitespace, and how it is stripped from text
     * @param escaper how entities in text and attributes are unescaped
     * @return an iterator over one DOM element at a time
     */
    default CheckedIterator<XmlElement> iterateDom(final Reader in, final Charset charset
            , final boolean conserveWhitespace, final Trim trimmer, final UnEscape escaper) {
        return new CheckedIterator<>() {
            private String pending;

            public boolean hasNext() throws Exception {
                if (pending != null) return true;
                while (true) {
                    final Character next = readFirstNonWhiteChar(in);
                    if (next == null) return false;
                    if (next != '<') throw new InvalidXml();
                    final String xml = readUntilCurrentTagIsClosed(in);
                    // a comment, prolog, declaration or section sitting between two elements
                    // belongs to neither, and an element is what this iterator hands back, so
                    // whatever was read past looking for one is read past again
                    if (isBetweenContent(xml)) continue;
                    pending = xml;
                    return true;
                }
            }

            public XmlElement next() throws Exception {
                if (!hasNext()) throw new NoSuchElementException("No more elements to read");
                final String xml = pending;
                pending = null;
                try (final var reader = newStreamReader(xml, charset)) {
                    return XmlReader.toXmlDom(reader, conserveWhitespace, trimmer, escaper);
                }
            }
        };
    }

    /**
     * Hands back each top level element of a document in turn as an object of the given class,
     * each one read into a DOM tree the way {@link #iterateDom} would and then handed to the
     * reader, which brings its own deserializers, caches and validators.
     *
     * @param <T> the type of object to read each element into
     * @param in the stream to read the elements from
     * @param charset the charset the element text is decoded with before it is parsed
     * @param reader the reader to fill each object with
     * @param clazz the class to fill
     * @param conserveWhitespace whether whitespace in text is kept instead of trimmed
     * @param trimmer what counts as whitespace, and how it is stripped from text
     * @param escaper how entities in text and attributes are unescaped
     * @return an iterator over one object at a time
     */
    default <T> CheckedIterator<T> iterateObject(final Reader in, final Charset charset, final XmlReader reader
            , final Class<T> clazz, final boolean conserveWhitespace, final Trim trimmer, final UnEscape escaper) {
        return new CheckedIterator<>() {
            private String pending;

            public boolean hasNext() throws Exception {
                if (pending != null) return true;
                while (true) {
                    final Character next = readFirstNonWhiteChar(in);
                    if (next == null) return false;
                    if (next != '<') throw new InvalidXml();
                    final String xml = readUntilCurrentTagIsClosed(in);
                    // an element is what this iterator hands back, so a comment, prolog or
                    // declaration sitting between two of them is read past again
                    if (isBetweenContent(xml)) continue;
                    pending = xml;
                    return true;
                }
            }

            public T next() throws Exception {
                if (!hasNext()) throw new NoSuchElementException("No more elements to read");
                final String xml = pending;
                pending = null;
                try (final var item = newStreamReader(xml, charset)) {
                    final XmlElement element = XmlReader.toXmlDom(item, conserveWhitespace, trimmer, escaper);
                    return reader.domToObject(element, clazz);
                }
            }
        };
    }

    /**
     * Reads up to the first character that is none of a space, a tab, a line feed nor a carriage
     * return, taking it in rather than leaving it for the next read.
     *
     * @param in the stream to read from
     * @return the first non-white character, or null at the end of the stream
     * @throws IOException if the stream cannot be read
     */
    static Character readFirstNonWhiteChar(final Reader in) throws IOException {
        int r;
        while ((r = in.read()) != -1) {
            final char c = (char) r;
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') continue;
            return c;
        }
        return null;
    }

    /**
     * Reads one element, whose '&lt;' the caller has already taken in. A comment, a prolog or a
     * section that sits between two elements belongs to neither of them, so it is read past, and
     * one that sits inside an element is copied through as it is. None of the three counts as a
     * tag, so a '&lt;' that one of them holds is not mistaken for the start of a nested element.
     *
     * @param in the stream to read from, whose '&lt;' has already been taken in
     * @return the element as it was written, or what was read past when no element was found
     * @throws IOException if the stream cannot be read
     */
    static String readUntilCurrentTagIsClosed(final Reader in) throws IOException {
        final StringBuilder element = new StringBuilder();
        final StringBuilder between = new StringBuilder();
        int numberOfTagsOpened = 0;
        // a prolog whose read ended at a '>' in its data carries on into the text that follows
        boolean insideProlog = false;

        // the '<' of the first tag has been taken in, so its tag is read from here on
        String head = readTag(in);
        if (head == null) throw new InvalidXml("Unclosed tag");

        while (head != null) {
            final String trimmed = head.trim();
            if (trimmed.isEmpty()) throw new InvalidXml("Unclosed tag");

            // what is read belongs to the element only once one of them is open, and until then it
            // is held apart, so that a stream of nothing but comments still has something to give
            final StringBuilder outer = holderOf(element, between, numberOfTagsOpened);

            if (head.startsWith(XML_START_CDATA)) {
                // a section holds whatever it likes: it is character data and never a tag
                final String cdata = readCData(in, head);
                outer.append(LESS_THAN).append(XML_START_CDATA).append(cdata)
                     .append(XML_END_CDATA).append(GREATER_THAN);

            } else if (head.startsWith(XML_START_COMMENT)) {
                // a comment holds anything at all, so it is finished off before it is sorted out
                outer.append(finishComment(in, head));

            } else if (trimmed.charAt(0) == XML_PROLOG) {
                // a prolog belongs to no element at all, and it is not necessarily finished by
                // the '>' the read above stopped at: whether it carried on is decided by the
                // run of text that follows
                outer.append(LESS_THAN).append(head).append(GREATER_THAN);
                insideProlog = !head.endsWith(String.valueOf(XML_PROLOG));

            } else if (trimmed.charAt(0) == CHAR_EXCLAMATION_MARK) {
                // neither does a declaration, and a DOCTYPE's internal subset carries on past
                // the '>' that ended this read
                outer.append(LESS_THAN).append(head).append(GREATER_THAN);
                final String rest = finishTag(in, head);
                if (rest != null) outer.append(rest);

            } else if (trimmed.charAt(0) == CHAR_FORWARD_SLASH) {
                element.append(LESS_THAN).append(head).append(GREATER_THAN);
                if (--numberOfTagsOpened == 0) break;

            } else if (isSelfClosing(trimmed)) {
                element.append(LESS_THAN).append(head).append(GREATER_THAN);
                if (numberOfTagsOpened == 0) break;

            } else {
                element.append(LESS_THAN).append(head).append(GREATER_THAN);
                numberOfTagsOpened++;
            }

            if (numberOfTagsOpened < 0)
                throw new InvalidXml("Unbalanced tags: closing tag without a matching opening tag");

            // the element carries on with its character data, and then with its next tag
            String text = readText(in);
            if (text == null) break;
            if (insideProlog) {
                // only what follows the '?>' that really ends the prolog belongs to the document
                insideProlog = false;
                final int end = text.indexOf(XML_END_PROLOG);
                if (end >= 0) text = text.substring(end + XML_END_PROLOG.length());
            }
            holderOf(element, between, numberOfTagsOpened).append(text);
            head = readTag(in);
        }

        return element.length() > 0 ? element.toString() : between.toString();
    }

    /**
     * Tells whether what was read is only what sits between two elements rather than an element
     * itself: a comment, a prolog, a declaration or a section, none of which starts with the
     * name of an element.
     *
     * @param xml what was read
     * @return true when it holds no element
     */
    private static boolean isBetweenContent(final String xml) {
        return xml.startsWith(LESS_THAN + XML_PROLOG) || xml.startsWith(LESS_THAN + CHAR_EXCLAMATION_MARK);
    }

    /** What has been read belongs to the element only once one of them is open. */
    private static StringBuilder holderOf(final StringBuilder element, final StringBuilder between,
                                          final int numberOfTagsOpened) {
        return numberOfTagsOpened > 0 ? element : between;
    }

    /** A tag that opens and closes at once, so it leaves the count of open ones alone. */
    private static boolean isSelfClosing(final String trimmedHead) {
        return trimmedHead.charAt(trimmedHead.length() - 1) == CHAR_FORWARD_SLASH;
    }

    /**
     * Reads the character data that carries on after a tag, up to the start of the next one.
     *
     * @return everything read, without the '&lt;' that starts the next tag, or null at the end
     */
    private static String readText(final Reader in) throws IOException {
        final StringBuilder chars = new StringBuilder();
        int data; while ((data = in.read()) != -1) {
            if (data == XML_TAG_START) return chars.toString();
            chars.append((char) data);
        }
        return null;
    }

    /**
     * Reads the rest of a comment, whose head has already been read up to a '&gt;'. A comment ends
     * at a '--&gt;' and can hold a '&gt;' before that, so the head alone does not tell whether the
     * comment is already finished.
     *
     * @param head everything read since the '&lt;' of the comment
     *         the whole comment, from that '&lt;' up to the '&gt;' which ends it
     */
    private static String finishComment(final Reader in, final String head) throws IOException {
        final StringBuilder comment = new StringBuilder(LESS_THAN).append(head);
        if (head.endsWith(XML_END_COMMENT)) return comment.append(GREATER_THAN).toString();

        comment.append(GREATER_THAN);
        final String end = XML_END_COMMENT + GREATER_THAN;
        int data; while ((data = in.read()) != -1) {
            comment.append((char) data);
            if (comment.length() >= end.length()
                    && comment.substring(comment.length() - end.length()).equals(end))
                return comment.toString();
        }
        throw new InvalidXml("Unterminated comment");
    }

}
