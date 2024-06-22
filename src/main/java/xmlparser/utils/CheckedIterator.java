package xmlparser.utils;

public interface CheckedIterator<T> {
    boolean hasNext() throws Exception;
    T next() throws Exception;
}
