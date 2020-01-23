package xmlparser.utils;

import java.io.IOException;
import java.io.InputStreamReader;

import static xmlparser.utils.Constants.CHAR_FORWARD_SLASH;
import static xmlparser.utils.Constants.CHAR_SPACE;

public enum XmlParse {;

    public static String getNameOfTag(final String tag) {
        int offset = 0;
        for (; offset < tag.length(); offset++) {
            if (tag.charAt(offset) == CHAR_SPACE || tag.charAt(offset) == CHAR_FORWARD_SLASH)
                break;
        }
        return tag.substring(0, offset);
    }

    public static String readLine(final InputStreamReader in, final char end) throws IOException {
        final StringBuilder chars = new StringBuilder();
        int data;
        while ((data = in.read()) != -1) {
            if (data == end) break;
            chars.append((char)data);
        }
        if (data == -1) return null;
        return chars.toString();
    }

    public static String readUntil(final InputStreamReader in, final String end) throws IOException {
        final StringBuilder chars = new StringBuilder();

        // read the first few bytes that can't match yet
        for (int i = 0; i < end.length(); i++) {
            int data = in.read();
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

    public static int indexOfNonWhitespaceChar(final String input, final int offset) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (at == ' ' || at == '\t' || at == '\n' || at == '\r') continue;
            return i;
        }
        return -1;
    }
    public static int indexOfWhitespaceChar(final String input, final int offset) {
        for (int i = offset; i < input.length(); i++) {
            final char at = input.charAt(i);
            if (at == ' ' || at == '\t' || at == '\n' || at == '\r') return i;
        }
        return -1;
    }

}
