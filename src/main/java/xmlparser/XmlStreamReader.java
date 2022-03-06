package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.parsing.EventParser;
import xmlparser.utils.Escaping.UnEscape;
import xmlparser.utils.Trimming.Trim;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import static xmlparser.utils.Constants.*;
import static xmlparser.utils.XmlParse.*;

public interface XmlStreamReader {

    static void toXmlStream(final InputStreamReader in, final EventParser parser, final Trim trimmer, final UnEscape escaper) throws IOException {
        boolean isStart = true;
        String str; while ((str = readLine(in, XML_TAG_START)) != null) {
            final String text = trimmer.trim(str);
            if (!text.isEmpty()) {
                if (isStart) throw new InvalidXml("XML contains non-whitespace characters before opening tag");
                parser.someText(escaper.unescape(text));
            }
            isStart = false;

            str = trimmer.trim(readLine(in, XML_TAG_END));
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            if (str.startsWith(XML_START_COMMENT)) {
                if (str.endsWith(XML_END_COMMENT))
                    continue;
                readUntil(in, XML_END_COMMENT + GREATER_THAN);
                continue;
            }

            if (str.charAt(0) == XML_PROLOG) continue;
            if (str.charAt(0) == CHAR_FORWARD_SLASH) parser.endNode(false);
            else {
                final String name = getNameOfTag(str);
                if (str.length() == name.length()) {
                    parser.startNode(str, new HashMap<>());
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();
                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr, end-1), trimmer, escaper));
                    parser.endNode(true);
                } else {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr+1, end), trimmer, escaper));
                }
            }
        }
    }

    static HashMap<String, String> xmlToAttributes(String input, final Trim trimmer, final UnEscape escaper) {
        final HashMap<String, String> attributes = new HashMap<>();

        while (!input.isEmpty()) {
            int startName = indexOfNonWhitespaceChar(input, 0, trimmer);
            if (startName == -1) break;
            int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) break;

            final String name = trimmer.trim(input.substring(startName, equals));
            input = input.substring(equals+1);

            int startValue = indexOfNonWhitespaceChar(input, 0, trimmer);
            if (startValue == -1) break;

            int endValue; final String value;
            if (input.charAt(startValue) == CHAR_DOUBLE_QUOTE) {
                startValue++;
                endValue = input.indexOf(CHAR_DOUBLE_QUOTE, startValue);
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
