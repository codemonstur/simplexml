package xmlparser.parsing;

import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;

import java.util.Map;

/**
 * An {@link EventParser} that builds a tree as it is told about it, keeping hold of the element
 * it is currently inside so that the next one becomes a child of it. This is what the reader
 * uses to turn a stream into a DOM, and a closing tag with nothing open is noticed here.
 */
public class DomBuilder implements EventParser {
    private XmlElement root;
    private XmlElement current;

    /** Starts with an empty tree, ready for the first event. */
    public DomBuilder() { }

    public void startNode(final String name, final Map<String, String> attrs) {
        final XmlElement tmp = new XmlElement(this.current, name, attrs);

        if (this.current != null) this.current.appendChild(tmp);
        else this.root = tmp;

        this.current = tmp;
    }
    public void endNode(final boolean selfClosing) {
        if (this.current == null)
            throw new InvalidXml("Unbalanced tags: closing tag without matching opening tag");
        this.current.setSelfClosing(selfClosing);
        this.current = this.current.parent;
    }
    public void someText(final String txt) {
        if (txt == null) return;
        if (this.current == null)
            throw new InvalidXml("Character data outside of any element: " + txt);

        this.current.children.add(new XmlTextElement(this.current, txt));
    }
    /**
     * Reports whether no tag is open at the moment: true at the start, and again once the root
     * has closed. The reader checks it after the last event, where a false means a tag was left
     * unclosed.
     * @return true while outside every element
     */
    public boolean isCurrentNull() {
        return current == null;
    }
    /**
     * Reports whether any element has been seen at all, which tells an empty document from one
     * that has a root.
     * @return true until the first startNode event
     */
    public boolean isRootNull() {
        return root == null;
    }

    /**
     * Gives the outermost element built so far, which is the whole tree once the document has
     * been read.
     * @return the root element, or null when no tag has been opened
     */
    public XmlElement getRoot() {
        return root;
    }


}
