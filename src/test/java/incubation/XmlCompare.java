package incubation;

import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public enum XmlCompare {;

    private static final boolean
        tagNameIsEqual = true,
        attributeNamesAreEqual = true,
        attributeValuesAreEqual = true,
        attributeOrderIsEqual = true,
        childrenAreEqual = true,
        childrenOrderIsEqual = true;

    public boolean equals(final XmlElement first, final XmlElement second) {
        if (first instanceof XmlTextElement) {
            if (!(second instanceof XmlTextElement))
                return false;
            return ((XmlTextElement)first).text.equals(((XmlTextElement) second).text);
        }

        if (tagNameIsEqual && !Objects.equals(first.name, second.name)) return false;
        if (attributeNamesAreEqual && !Objects.equals(first.attributes.keySet(), second.attributes.keySet())) return false;
        if (attributeValuesAreEqual) {
            for (final Map.Entry<String, String> entry : first.attributes.entrySet()) {
                if (!Objects.equals(entry.getValue(), second.attributes.get(entry.getKey())))
                    return false;
            }
        }
        if (attributeOrderIsEqual) {
            final Iterator<Entry<String, String>> firstAttributes = first.attributes.entrySet().iterator();
            final Iterator<Entry<String, String>> secondAttributes = second.attributes.entrySet().iterator();
            while (firstAttributes.hasNext() && secondAttributes.hasNext()) {
                if (!firstAttributes.next().getKey().equals(secondAttributes.next().getKey()))
                    return false;
            }
        }
        if (childrenAreEqual && first.children.size() != second.children.size()) return false;

        if (childrenAreEqual) {
            for (int i = 0; i < first.children.size(); i++) {
                if (!equals(first.children.get(i), second.children.get(i)))
                    return false;
            }
        }
        return true;
    }

}
