package simplexml.model;

import java.util.*;

public class Element {

    public Element parent;
    public String name;
    public Map<String, String> attributes;
    public List<Element> children;
    public String text;

    public Element(final Element parent, final String name,
                   final Map<String, String> attributes, final String text) {
        this(parent, name, attributes, new LinkedList<>(), text);
    }
    public Element(final Element parent, final String name, final Map<String, String> attributes,
                   final List<Element> children, final String text) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.children = children;
        this.text = text;
    }

    public void appendChild(final Element child) {
        this.children.add(child);
    }

    public Element findChildForName(final String name, final Element _default) {
        for (final Element child : children) {
            if (name.equals(child.name))
                return child;
        }
        return _default;
    }

    public int numChildrenWithName(final String name) {
        int num = 0;
        for (final Element child : children) {
            if (name.equals(child.name)) num++;
        }
        return num;
    }


    public static Element element(final String name) {
        return new Element(null, name, new HashMap<>(), new ArrayList<>(), null);
    }
    public Element child(final Element child) {
        this.children.add(child);
        child.parent = this;
        return this;
    }
    public Element attribute(final String name, final String value) {
        attributes.put(name, value);
        return this;
    }
    public Element text(final String text) {
        this.text = text;
        return this;
    }

}

