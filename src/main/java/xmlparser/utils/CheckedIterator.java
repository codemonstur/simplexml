package xmlparser.utils;

/**
 * An iterator that is allowed to throw. Reading from a file can fail at any moment, and rather
 * than wrap every failure in something that has to be unwrapped again, the exception is left
 * where it is for the caller to deal with.
 *
 * @param <T> the type of element the iterator hands out
 */
public interface CheckedIterator<T> {
    /**
     * Tells whether another element is waiting to be read.
     *
     * @return true when next() would give another element
     * @throws Exception when looking ahead at the input fails
     */
    boolean hasNext() throws Exception;
    /**
     * Reads the next element.
     *
     * @return the next element
     * @throws Exception when reading the input fails
     */
    T next() throws Exception;
}
