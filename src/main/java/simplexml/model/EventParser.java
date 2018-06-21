package simplexml.model;

import java.util.Map;

public final class EventParser {
    private Element root;
    private Element current;

    public void startNode(final String name, final Map<String, String> attrs) {
        final Element tmp = new Element(this.current, name, attrs, null);

        if (this.current != null) this.current.appendChild(tmp);
        else this.root = tmp;

        this.current = tmp;
    }
    public void endNode() {
        this.current = this.current.parent;
    }
    public void someText(final String txt) {
        if (txt != null && !txt.isEmpty())
            this.current.text = txt;
    }
    public Element getRoot() {
        return this.root;
    }

}

