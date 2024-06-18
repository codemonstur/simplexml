package utils;

import java.util.HashMap;
import java.util.Map;

public enum Functions {;

    public static <T, U> Map<T, U> mapOf(final T key1, final U value1) {
        final Map<T, U> map = new HashMap<>();
        map.put(key1, value1);
        return map;
    }

}
