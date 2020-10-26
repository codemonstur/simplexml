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
        catch (ParseException e) {
            return null;
        }
    }

    public static <K, V> MapBuilder<K, V> newHashMap() {
        return new MapBuilder<K, V>(new HashMap<>());
    }
    public static class MapBuilder<K, V> {
        private final Map<K, V> map;
        public MapBuilder(final Map<K, V> map) {
            this.map = map;
        }
        public MapBuilder<K, V> put(K k, V v) {
            this.map.put(k, v);
            return this;
        }
        public Map<K, V> build() {
            return this.map;
        }
    }

}
