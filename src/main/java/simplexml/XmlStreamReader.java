package simplexml;

import simplexml.error.InvalidXml;
import simplexml.parsing.EventParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import static simplexml.utils.Constants.*;
import static simplexml.utils.Functions.trim;
import static simplexml.utils.XML.unescapeXml;
import static simplexml.utils.XmlParse.*;

public interface XmlStreamReader {

    static void toXmlStream(final InputStreamReader in, final EventParser parser) throws IOException {
        String str;
        while ((str = readLine(in, XML_TAG_START)) != null) {
            if (!str.isEmpty()) parser.someText(unescapeXml(str.trim()));

            str = trim(readLine(in, XML_TAG_END));
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            if (str.charAt(0) == XML_PROLOG) continue;

            if (str.charAt(0) == XML_SELF_CLOSING) parser.endNode();
            else {
                final String name = getNameOfTag(str);
                if (str.length() == name.length()) {
                    parser.startNode(str, new HashMap<>());
                    continue;
                }

                final int beginAttr = name.length();
                final int end = str.length();
                if (str.endsWith(FORWARD_SLASH)) {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr, end-1)));
                    parser.endNode();
                } else {
                    parser.startNode(name, xmlToAttributes(str.substring(beginAttr+1, end)));
                }
            }
        }
    }

    static HashMap<String, String> xmlToAttributes(String input) {
        final HashMap<String, String> attributes = new HashMap<>();

        while (!input.isEmpty()) {
            int startName = indexOfNonWhitespaceChar(input, 0);
            if (startName == -1) break;
            int equals = input.indexOf(CHAR_EQUALS, startName+1);
            if (equals == -1) break;

            final String name = input.substring(startName, equals).trim();
            input = input.substring(equals+1);

            int startValue = indexOfNonWhitespaceChar(input, 0);
            if (startValue == -1) break;

            int endValue; final String value;
            if (input.charAt(startValue) == CHAR_DOUBLE_QUOTE) {
                startValue++;
                endValue = input.indexOf(CHAR_DOUBLE_QUOTE, startValue);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue).trim();
            } else {
                endValue = indexOfWhitespaceChar(input, startValue+1);
                if (endValue == -1) endValue = input.length()-1;
                value = input.substring(startValue, endValue+1).trim();
            }

            input = input.substring(endValue+1);

            attributes.put(name, unescapeXml(value));
        }

        return attributes;
    }

}
