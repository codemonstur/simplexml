package xmlparser.utils;

import xmlparser.XmlParser;
import xmlparser.parsing.ObjectDeserializer;
import xmlparser.parsing.ObjectSerializer;

public enum Interfaces {;

    public interface AccessXmlParser {
        XmlParser getXmlParser();
    }
    public interface AccessSerializers {
        boolean hasSerializer(Class<?> type);
        ObjectSerializer getSerializer(Class<?> type);
    }
    public interface AccessDeserializers {
        ObjectDeserializer getDeserializer(Class<?> type);
    }
    public interface ParserConfiguration {
        boolean shouldEncodeUTF8();
        boolean shouldPrettyPrint();
    }
    public interface CheckedIterator<T> {
        boolean hasNext() throws Exception;
        T next() throws Exception;
    }

}
