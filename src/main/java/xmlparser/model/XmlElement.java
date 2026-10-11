package xmlparser.model;

import java.util.*;

import static xmlparser.utils.Functions.isNullOrEmpty;

/**
 * One node of a parsed document: a name, the attributes that were on its tag, and its children
 * in the order they appeared. Text is a child too, held in an {@link XmlTextElement}, so an
 * element carrying both text and elements keeps them in reading order.
 */
public class XmlElement {

    /**
     * The element this one was opened inside, null for the root of a document or for a node that
     * was built on its own.
     */
    public XmlElement parent;
    /**
     * The name the tag was written with, null for a run of text.
     */
    public String name;
    /**
     * The attributes the tag carried, keyed by name, empty when it had none.
     */
    public Map<String, String> attributes;
    /**
     * The children in the order they appeared, text runs counted among them as
     * {@link XmlTextElement}s.
     */
    public List<XmlElement> children;
    private boolean selfClosing;

    /**
     * Creates an element with no children yet, so tags can be appended to it as they are read.
     * @param parent the element it belongs to, or null for a root
     * @param name the tag name
     * @param attributes the attributes the tag carried
     */
    public XmlElement(final XmlElement parent, final String name,
                      final Map<String, String> attributes) {
        this(parent, name, attributes, new LinkedList<>());
    }
    /**
     * Creates an element around a list of children that already exists.
     * @param parent the element it belongs to, or null for a root
     * @param name the tag name
     * @param attributes the attributes the tag carried
     * @param children the children, kept as given rather than copied
     */
    public XmlElement(final XmlElement parent, final String name, final Map<String, String> attributes,
                      final List<XmlElement> children) {
        this.parent = parent;
        this.name = name;
        this.attributes = attributes;
        this.children = children;
    }

    /**
     * Reports whether this element's tag closed itself, as {@code <tag/>}, instead of having a
     * separate closing tag. False until an end event says otherwise.
     * @return true if the tag was self-closing
     */
    public boolean isSelfClosing() {
        return selfClosing;
    }
    /**
     * Records how this element's tag closed, called as the closing event is handled.
     * @param selfClosing true if the tag closed itself
     */
    public void setSelfClosing(final boolean selfClosing) {
        this.selfClosing = selfClosing;
    }

    /**
     * Adds a child at the end of the children list, leaving the parent links of both elements
     * exactly as they were.
     * @param child the child to add
     */
    public void appendChild(final XmlElement child) {
        this.children.add(child);
    }

    /**
     * Finds the first direct child with the given name, looking at this element's own children
     * rather than the whole subtree.
     * @param name the child tag name to look for
     * @param defaultValue the value to give back when no child has that name
     * @return the first child with that name, or defaultValue
     */
    public XmlElement findChildForName(final String name, final XmlElement defaultValue) {
        return findChildForName(this, name, defaultValue);
    }

    /**
     * Finds the first direct child of an element that may itself be null: a null element, or one
     * with no child of that name, gives back the default rather than failing.
     * @param element the element to search, may be null
     * @param name the child tag name to look for
     * @param defaultValue the value to give back when there is nothing to find
     * @return the first matching child, or defaultValue
     */
    public static XmlElement findChildForName(final XmlElement element, final String name, final XmlElement defaultValue) {
        if (element == null) return defaultValue;
        for (final XmlElement child : element.children) {
            if (name.equals(child.name))
                return child;
        }
        return defaultValue;
    }

    /**
     * Counts the direct children with the given name, again without descending into the subtree.
     * @param name the child tag name to count
     * @return how many children have that name, 0 when none do
     */
    public int numChildrenWithName(final String name) {
        int num = 0;
        for (final XmlElement child : children) {
            if (name.equals(child.name)) num++;
        }
        return num;
    }

    /**
     * Gives the text this element holds, joining every text child in the order it appeared.
     * @return the joined text, or null when there is no text child
     */
    public String getText() {
        final StringBuilder builder = new StringBuilder();
        for (final XmlElement child : children) {
            if (child instanceof XmlTextElement)
                builder.append(((XmlTextElement)child).text);
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    /**
     * Replaces the text of this element: every text child is dropped first, then a single one is
     * added unless the given text is null or empty. Element children are left untouched.
     * @param text the new text, or null/empty to leave the element without any
     */
    public void setText(final String text) {
        children.removeIf(xmlElement -> xmlElement instanceof XmlTextElement);
        if (!isNullOrEmpty(text)) children.add(new XmlTextElement(this, text));
    }

    /**
     * Reports whether anything is nested inside this element beyond a run of text.
     * @return true when at least one child is not a text element
     */
    public boolean hasNonTextChildren() {
        if (children.isEmpty()) return false;
        for (final XmlElement e : children) {
            if (e instanceof XmlTextElement) continue;
            return true;
        }
        return false;
    }

    /**
     * Collects every element with the given name in this element's subtree, walking down from
     * this element, which is included itself when its name matches, in document order.
     * @param name the tag name to collect
     * @return the matching elements, empty when the subtree has none
     */
    public List<XmlElement> getElementsByTagName(final String name) {
        final List<XmlElement> list = new ArrayList<>();
        getElementsByTagName(this, name, list);
        return list;
    }
    private static void getElementsByTagName(final XmlElement element, final String name, final List<XmlElement> list) {
        if (element == null) return;
        if (name.equals(element.name)) list.add(element);
        if (element.children == null) return;
        for (final XmlElement child : element.children) {
            getElementsByTagName(child, name, list);
        }
    }
    
    @Override
    public String toString() {
        return "XmlElement[" + name + "]";
    }

    /**
     * A run of text, kept as a child of the element it appeared in rather than as a field on it. It
     * has no name, no attributes and no children of its own, and its text is final: setText is
     * inherited from XmlElement but has nothing to change here. Written on its own it gives back
     * just its own escaped text.
     */
    public static class XmlTextElement extends XmlElement {
        /**
         * The character data this run holds, kept exactly as it was given.
         */
        public final String text;
        /**
         * Holds a run of character data as a child of the element it appeared in.
         * @param parent the element the text appears in
         * @param text the character data to hold
         */
        public XmlTextElement(final XmlElement parent, final String text) {
            // a text element carries no name and holds no attributes or children of its own, but
            // they are kept empty rather than left out so that the inherited methods still work
            super(parent, null, new HashMap<>(), new LinkedList<>());
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "XmlTextElement[" + text + "]";
        }
    }


    /**
     * Starts a builder for an element with the given name, the short form of creating an
     * {@link XmlElementBuilder} yourself.
     * @param name the tag name of the element to build
     * @return a builder to add attributes, text and children to
     */
    public static XmlElementBuilder newElement(final String name) {
        return new XmlElementBuilder(name);
    }

    /**
     * Assembles an {@link XmlElement} a piece at a time, starting from its name. Attributes, text
     * and children can be added in any order, each returns this so the calls chain, and
     * {@link #build()} hands over the finished element.
     */
    public static class XmlElementBuilder {
        private XmlElement element;

        /**
         * Starts an element with the given name, no parent and no attributes yet.
         * @param name the tag name of the element being built
         */
        public XmlElementBuilder(final String name) {
            this.element = new XmlElement(null, name, new HashMap<>());
        }
        /**
         * Puts an attribute on the element being built, overwriting a value already stored under
         * that name.
         * @param name the attribute name
         * @param value the attribute value
         * @return this builder, for chaining
         */
        public XmlElementBuilder attribute(final String name, final String value) {
            element.attributes.put(name, value);
            return this;
        }
        /**
         * Sets the text of the element being built, replacing any text already there; null or
         * empty text just clears it out.
         * @param text the text to hold
         * @return this builder, for chaining
         */
        public XmlElementBuilder text(final String text) {
            element.setText(text);
            return this;
        }
        /**
         * Appends a child to the element being built and points the child's parent back at it.
         * @param child the child to add
         * @return this builder, for chaining
         */
        public XmlElementBuilder child(final XmlElement child) {
            element.children.add(child);
            child.parent = element;
            return this;
        }
        /**
         * Builds what another builder has assembled so far and appends the result as a child.
         * @param builder the builder to take the child from
         * @return this builder, for chaining
         */
        public XmlElementBuilder child(final XmlElementBuilder builder) {
            return child(builder.build());
        }
        /**
         * Hands over the element assembled so far, the same instance on every call.
         * @return the element this builder has been working on
         */
        public XmlElement build() {
            return element;
        }
    }

}

