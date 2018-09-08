package simplexml.utils;

import java.text.NumberFormat;
import java.text.ParseException;

public enum Builder {;

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    public static Number toNumber(final String s) {
        try {
            return NUMBER_FORMAT.parse(s);
        }
        catch (ParseException e) {
            return null;
        }
    }

}
