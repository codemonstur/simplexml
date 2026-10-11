package xmlparser.parsing;

import java.util.Map;

/**
 * Receives a document as it is read: a name and its attributes when a tag opens, a word about
 * whether a tag closes by itself when it closes, and the text in between. The events arrive in
 * document order, and it is up to the implementation to make something of them.
 */
public interface EventParser {

    /**
     * Reports an opening tag: the name it was written with and the attributes it carried, an
     * empty map when it had none. A self-closing tag is followed straight away by its endNode.
     * @param name the name of the tag that opened
     * @param attrs the attributes on the tag, empty when there are none
     */
    void startNode(String name, Map<String, String> attrs);
    /**
     * Reports a closing tag, saying whether it was the closing half of a self-closing tag rather
     * than a separate closing tag of its own.
     * @param selfClosing true if the tag closed itself
     */
    void endNode(boolean selfClosing);
    /**
     * Reports a run of character data found between tags, handed over as it was read after
     * unescaping and whatever trimming the reader was asked to do.
     * @param txt the text that was read, possibly null and safe to ignore
     */
    void someText(String txt);

}

