package xmlparser.utils;

import xmlparser.parsing.ObjectDeserializer;

public interface AccessDeserializers {
    ObjectDeserializer getDeserializer(Class<?> type);
}
