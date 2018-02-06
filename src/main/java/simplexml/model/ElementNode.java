package simplexml.model;

import java.util.*;

public class ElementNode {

    public ElementNode parent;
    public String name;
    public Map<String, String> attributes;
    public List<ElementNode> children;
    public String text;

    public ElementNode(final ElementNode parent, final String name,
                       final Map<String, String> attributes, final String text) {
        this(parent, name, attributes, new LinkedList<>(), text);
    }
    public ElementNode(final ElementNode parent, final String name, final Map<String, String> attributes,
                       final List<ElementNode> children, final String text) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.children = children;
        this.text = text;
    }

    public void appendChild(final ElementNode child) {
        this.children.add(child);
    }


    public static ElementNode element(final String name) {
        return new ElementNode(null, name, new HashMap<>(), new ArrayList<>(), null);
    }
    public ElementNode child(final ElementNode child) {
        this.children.add(child);
        child.parent = this;
        return this;
    }
    public ElementNode attribute(final String name, final String value) {
        attributes.put(name, value);
        return this;
    }
    public ElementNode text(final String text) {
        this.text = text;
        return this;
    }

}

