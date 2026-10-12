package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.parsing.EventParser;
import xmlparser.utils.Escaping.UnEscape;
import xmlparser.utils.Trimming.Trim;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.XML.isValidTagName;
import static xmlparser.utils.XmlParse.*;

/**
 * Reads a document and reports it to an {@link EventParser} as it goes: one event for an opening
 * tag, one for a closing tag, one for a run of text. It keeps the names of the tags that are
 * currently open, which is how unbalanced input is caught here rather than turning into a
 * lopsided tree further down.
 */
public interface XmlStreamReader {

    /**
     * Reads the document and reports it to the parser as it goes: an event when a tag opens, one
     * when it closes, and one for each run of text between them, with CDATA read as text and
     * comments and prologs read past. The input is checked as it is read, so a closing tag that
     * matches no opening one, a second root element or content after the root has closed stop it
     * with InvalidXml rather than reaching the parser. The reader is not closed.
     *
     * @param in the reader to read the document from
     * @param parser the events are reported to
     * @param conserveWhitespace whether the whitespace between tags is reported as it was written
     * @param trimmer the opinion of whitespace to read with
     * @param escaper the unescape text and attribute values go through on their way to the parser
     * @throws IOException if reading fails
     * @throws InvalidXml if the document is not well formed
     */
    static void toXmlStream(final Reader in, final EventParser parser, final boolean conserveWhitespace,
                            final Trim trimmer, final UnEscape escaper) throws IOException {
        boolean isStart = true;
        boolean rootClosed = false;
        // a prolog whose read ended at a '>' in its data carries on into the text that follows,
        // which is looked at rather than taken as content
        boolean insideProlog = false;
        final var openTags = new ArrayDeque<String>();
        String str; while ((str = readLine(in, trimmer, XML_TAG_START)) != null) {
            if (insideProlog) {
                insideProlog = false;
                // the '?>' that really ends the prolog is in this run of text, and everything
                // up to it is the prolog's data rather than content of the document
                final int end = str.indexOf(XML_END_PROLOG);
                if (end >= 0) str = str.substring(end + XML_END_PROLOG.length());
            }
            final String text = trimmer.trim(str);
            if (!text.isEmpty()) {
                if (isStart) throw new InvalidXml("XML contains non-whitespace characters before opening tag");
                if (rootClosed) throw new InvalidXml("Content after the end of the root element: " + text);
                parser.someText(escaper.unescape( conserveWhitespace ? str : text));
            } else if (!isStart && !rootClosed && conserveWhitespace) {
                // whitespace after the root has closed belongs to no element, so it is dropped
                // rather than handed to a builder that has no element to hold it
                parser.someText(str);
            }

            final String head = readTag(in);
            if (head == null) throw new InvalidXml("Unclosed tag");
            // A CDATA section is literal character data: it runs to its own terminator and is
            // neither unescaped nor trimmed, so it must be read before the tag is interpreted
            if (head.startsWith(XML_START_CDATA)) {
                final String cdata = readCData(in, head);
                final boolean isBlank = trimmer.trim(cdata).isEmpty();
                if (isStart) {
                    if (!isBlank)
                        throw new InvalidXml("XML contains non-whitespace characters before opening tag");
                } else if (rootClosed) {
                    // a section holding nothing but whitespace after the root is not content
                    if (!isBlank) throw new InvalidXml("Content after the end of the root element: " + cdata);
                } else {
                    parser.someText(cdata);
                }
                continue;
            }

            str = trimmer.trim(head);
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            // A comment and a declaration are not content, and neither of the two is finished by
            // the first '>' that is not inside a quoted value: a comment can hold one in its data
            // and a declaration can carry on past it with the internal subset a DOCTYPE holds, so
            // whatever is left of one of them is read past here rather than mistaken for the rest
            // of the document.
            if (head.startsWith(XML_START_COMMENT) || str.charAt(0) == CHAR_EXCLAMATION_MARK) {
                finishTag(in, head);
                continue;
            }

            // A prolog ends at its '?>', but is allowed to end at a bare '>' instead. Which of
            // the two this one did is decided by the run of text that follows: it holds the '?>'
            // when the read above stopped at a '>' sitting in the prolog's data, and is content
            // of the document when it does not.
            if (str.charAt(0) == XML_PROLOG) {
                insideProlog = !head.endsWith(String.valueOf(XML_PROLOG));
                continue;
            }

            isStart = false;
            if (str.charAt(0) == CHAR_FORWARD_SLASH) {
                // the name of a closing tag may be preceded by whitespace: </ pojo >
                final String rest = str.substring(1);
                final int nameStart = Math.max(0, indexOfNonWhitespaceChar(rest, 0, trimmer));
                final String closingName = getNameOfTag(rest.substring(nameStart), trimmer);
                if (openTags.isEmpty())
                    throw new InvalidXml("Closing tag without a matching opening tag: </" + closingName + ">");
                final String expected = openTags.pop();
                if (!closingName.equals(expected))
                    throw new InvalidXml("Mismatched closing tag: expected </" + expected + "> but found </" + closingName + ">");
                parser.endNode(false);
                rootClosed = openTags.isEmpty();
            }
            else {
                final String name = getNameOfTag(str, trimmer);
                if (!isValidTagName(name)) throw new InvalidXml("Found opening tag with invalid characters: " + str);
                if (rootClosed)
                    throw new InvalidXml("A second root element was found: <" + name + "> follows the end of the first");

                if (str.length() == name.length()) {
                    openTags.push(name);
                    parser.startNode(str, new HashMap<>());
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();
                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr, end-1), trimmer, escaper));
                    parser.endNode(true);
                    rootClosed = openTags.isEmpty();
                } else {
                    openTags.push(name);
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr+1, end), trimmer, escaper));
                }
            }
        }
    }

    /**
     * Reads everything after the name of an opening tag into its attributes. A value is taken
     * from between matching quotes, single or double, or up to the next whitespace when it has
     * none, then trimmed and unescaped. A name with no '=', or a value that never starts, ends
     * the list and whatever is left of the input is dropped.
     *
     * @param input the inside of the tag, after its name
     * @param trimmer the opinion of whitespace to trim names and values with
     * @param escaper the unescape the values go through
     * @return the attributes, an empty map when the tag has none
     * @throws InvalidXml if a name is not followed by an '=', or a name is used twice
     */
    static HashMap<String, String> xmlToAttributes(String input, final Trim trimmer, final UnEscape escaper) {
        final HashMap<String, String> attributes = new HashMap<>();

        while (!input.isEmpty()) {
            final int startName = indexOfNonWhitespaceChar(input, 0, trimmer);
            if (startName == -1) break;
            final int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) throw new InvalidXml("Invalid formatted attribute list: '" + input + "'");

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

            // a name used twice in one tag is not a document this library can read: XML allows
            // it nowhere, and keeping the last of the two would hand back a value the document
            // never meant in either place
            if (attributes.put(name, escaper.unescape(value)) != null)
                throw new InvalidXml("Duplicate attribute: '" + name + "'");
        }

        return attributes;
    }

}
