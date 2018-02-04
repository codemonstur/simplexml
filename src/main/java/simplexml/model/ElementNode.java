package simplexml.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ElementNode {
    public ElementNode parent;
    public String name;
    public Map<String, String> attributes;
    public List<ElementNode> children = new LinkedList<>();
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

