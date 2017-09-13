package simplexml.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ElementNode {
    public final ElementNode parent;
    public final String name;
    public final Map<String, String> attributes;
    public final List<ElementNode> children = new LinkedList<>();
    public String text;

    public ElementNode(final ElementNode parent, final String name,
                       final Map<String, String> attributes, final String text) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.text = text;
    }

    public void appendChild(final ElementNode child) {
        this.children.add(child);
    }
}

