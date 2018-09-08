package simplexml;

import simplexml.error.InvalidXml;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static simplexml.utils.Constants.*;
import static simplexml.utils.Functions.trim;

public interface XmlCompress {

    default String compressXml(final String input) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            compressXML(new InputStreamReader(new ByteArrayInputStream(input.getBytes(UTF_8)), UTF_8), new OutputStreamWriter(out, UTF_8));
            return new String(out.toByteArray(), UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            // can't happen.
            return null;
        }
    }

    static void compressXML(final InputStreamReader in, final OutputStreamWriter out) throws IOException {
        String str;
        while ((str = readLine(in, XML_TAG_START)) != null) {
            if (!str.isEmpty()) out.write(str.trim());

            str = trim(readLine(in, XML_TAG_END));
            if (str.isEmpty()) throw new InvalidXml("Unclosed tag");
            if (str.charAt(0) == XML_PROLOG) continue;

            out.write(XML_TAG_START);
            if (str.charAt(0) == CHAR_FORWARD_SLASH) {
                out.write(CHAR_FORWARD_SLASH);
                out.write(getNameOfTag(str.substring(1)).trim());
                out.write(XML_TAG_END);
                continue;
            }

            final String name = getNameOfTag(str);
            if (str.length() == name.length()) {
                out.write(name);
                out.write(XML_TAG_END);
                continue;
            }

            final int beginAttr = name.length();
            final int end = str.length();
            out.write(name);
            if (str.endsWith(FORWARD_SLASH)) {
                parseAttributes(str.substring(beginAttr+1, end-1), out);
                out.write(FORWARD_SLASH);
            } else {
                parseAttributes(str.substring(beginAttr+1, end), out);
            }
            out.write(XML_TAG_END);
        }
        out.flush();
    }

    static String getNameOfTag(final String tag) {
        int offset = 0;
        for (; offset < tag.length(); offset++) {
            if (tag.charAt(offset) == CHAR_SPACE || tag.charAt(offset) == CHAR_FORWARD_SLASH)
                break;
        }
        return tag.substring(0, offset);
    }

    static String readLine(final InputStreamReader in, final char end) throws IOException {
        final List<Character> chars = new LinkedList<>();
        int data;
        while ((data = in.read()) != -1) {
            if (data == end) break;
            chars.add((char) data);
        }
        if (data == -1) return null;

        char[] value = new char[chars.size()];
        int i = 0;
        for (final Character c : chars) value[i++] = c;
        return new String(value);
    }

    static void parseAttributes(String input, final OutputStreamWriter out) throws IOException {

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

            out.write(CHAR_SPACE);
            out.write(name);
            out.write(CHAR_EQUALS);
            out.write(CHAR_DOUBLE_QUOTE);
            out.write(value);
            out.write(CHAR_DOUBLE_QUOTE);
        }
    }

    static int indexOfNonWhitespaceChar(final String input, final int offset) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (at == ' ' || at == '\t' || at == '\n' || at == '\r') continue;
            return i;
        }
        return -1;
    }
    static int indexOfWhitespaceChar(final String input, final int offset) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (at == ' ' || at == '\t' || at == '\n' || at == '\r') return i;
        }
        return -1;
    }

}
