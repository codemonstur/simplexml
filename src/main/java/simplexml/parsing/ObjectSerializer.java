package simplexml.parsing;

public interface ObjectSerializer {
    String convert(Object value);

    static ObjectSerializer defaultSerializer() {
        return Object::toString;
    }
}

