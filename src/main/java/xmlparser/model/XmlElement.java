package xmlparser.model;

import java.util.*;

import static xmlparser.utils.Functions.isNullOrEmpty;

public class XmlElement {

    public XmlElement parent;
    public String name;
    public Map<String, String> attributes;
    public List<XmlElement> children;

    public XmlElement(final XmlElement parent, final String name,
                      final Map<String, String> attributes) {
        this(parent, name, attributes, new LinkedList<>());
    }
    public XmlElement(final XmlElement parent, final String name, final Map<String, String> attributes,
                      final List<XmlElement> children) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.children = children;
    }

    public void appendChild(final XmlElement child) {
        this.children.add(child);
    }

    public XmlElement findChildForName(final String name, final XmlElement defaultValue) {
        return findChildForName(this, name, defaultValue);
    }

    public static XmlElement findChildForName(final XmlElement element, final String name, final XmlElement defaultValue) {
        if (element == null) return defaultValue;
        for (final XmlElement child : element.children) {
            if (name.equals(child.name))
                return child;
        }
        return defaultValue;
    }

    public int numChildrenWithName(final String name) {
        int num = 0;
        for (final XmlElement child : children) {
            if (name.equals(child.name)) num++;
        }
        return num;
    }

    public String getText() {
        final StringBuilder builder = new StringBuilder();
        for (final XmlElement child : children) {
            if (child instanceof XmlTextElement)
                builder.append(((XmlTextElement)child).text);
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    public void setText(final String text) {
        children.removeIf(xmlElement -> xmlElement instanceof XmlTextElement);
        if (!isNullOrEmpty(text)) children.add(new XmlTextElement(this, text));
    }

    public boolean hasNonTextChildren() {
        if (children.isEmpty()) return false;
        for (final XmlElement e : children) {
            if (e instanceof XmlTextElement) continue;
            return true;
        }
        return false;
    }

    public static class XmlTextElement extends XmlElement {
        public final String text;
        public XmlTextElement(final XmlElement parent, final String text) {
            super(parent, null, null, null);
            this.text = text;
        }
    }


    public static XmlElementBuilder newElement(final String name) {
        return new XmlElementBuilder(name);
    }
    public static class XmlElementBuilder {
        private XmlElement element;

        public XmlElementBuilder(final String name) {
            this.element = new XmlElement(null, name, new HashMap<>());
        }
        public XmlElementBuilder attribute(final String name, final String value) {
            element.attributes.put(name, value);
            return this;
        }
        public XmlElementBuilder text(final String text) {
            element.setText(text);
            return this;
        }
        public XmlElementBuilder child(final XmlElement child) {
            element.children.add(child);
            child.parent = element;
            return this;
        }
        public XmlElementBuilder child(final XmlElementBuilder builder) {
            return child(builder.build());
        }
        public XmlElement build() {
            return element;
        }
    }

}

