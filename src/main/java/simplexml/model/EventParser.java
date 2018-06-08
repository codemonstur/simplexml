package simplexml.model;

import java.util.Map;

public final class EventParser {
    private ElementNode root;
    private ElementNode current;

    public EventParser() {}

    public void startNode(final String name, final Map<String, String> attrs) {
        final ElementNode tmp = new ElementNode(this.current, name, attrs, null);

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
    public ElementNode getRoot() {
        return this.root;
    }

}

