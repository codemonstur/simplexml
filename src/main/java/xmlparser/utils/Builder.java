package xmlparser.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public enum Builder {;

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    public static Number toNumber(final String s) {
        try {
            return NUMBER_FORMAT.parse(s);
        }
        catch (final ParseException e) {
            return null;
        }
    }

}
