package simplexml.utils;

import simplexml.SimpleXml;
import simplexml.model.ObjectDeserializer;
import simplexml.model.ObjectSerializer;

public enum Interfaces {;

    public interface AccessSimpleXml {
        SimpleXml getSimpleXml();
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
