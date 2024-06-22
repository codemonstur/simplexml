package xmlparser.utils;

import xmlparser.parsing.ObjectSerializer;

public interface AccessSerializers {
    boolean hasSerializer(Class<?> type);
    ObjectSerializer getSerializer(Class<?> type);
}
