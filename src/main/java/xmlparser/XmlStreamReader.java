package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.parsing.EventParser;
import xmlparser.utils.Escaping.UnEscape;
import xmlparser.utils.Trimming.Trim;

import java.io.IOException;
import java.io.InputStreamReader;
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
    static void toXmlStream(final InputStreamReader in, final EventParser parser, final boolean conserveWhitespace,
                            final Trim trimmer, final UnEscape escaper) throws IOException {
        boolean isStart = true;
        boolean rootClosed = false;
        final var openTags = new ArrayDeque<String>();
        String str; while ((str = readLine(in, trimmer, XML_TAG_START)) != null) {
            final String text = trimmer.trim(str);
            if (!text.isEmpty()) {
                if (isStart) throw new InvalidXml("XML contains non-whitespace characters before opening tag");
                if (rootClosed) throw new InvalidXml("Content after the end of the root element: " + text);
                parser.someText(escaper.unescape( conserveWhitespace ? str : text));
            } else if (!isStart && conserveWhitespace) {
                parser.someText(str);
            }

            final String head = readTag(in);
            // A CDATA section is literal character data: it runs to its own terminator and is
            // neither unescaped nor trimmed, so it must be read before the tag is interpreted
            if (head != null && head.startsWith(XML_START_CDATA)) {
                final String cdata = readCData(in, head);
                if (!isStart) {
                    if (rootClosed) throw new InvalidXml("Content after the end of the root element: " + cdata);
                    parser.someText(cdata);
                }
                else if (!trimmer.trim(cdata).isEmpty())
                    throw new InvalidXml("XML contains non-whitespace characters before opening tag");
                continue;
            }

            str = trimmer.trim(head);
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            if (str.startsWith(XML_START_COMMENT)) {
                if (str.endsWith(XML_END_COMMENT))
                    continue;
                readUntil(in, XML_END_COMMENT + GREATER_THAN);
                continue;
            }

            if (str.charAt(0) == XML_PROLOG) continue;

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
     * @throws InvalidXml if a name is not followed by an '='
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

            attributes.put(name, escaper.unescape(value));
        }

        return attributes;
    }

}
