package xmlparser.parsing;

import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;

import java.util.Map;

public class DomBuilder implements EventParser {
    private XmlElement root;
    private XmlElement current;

    public void startNode(final String name, final Map<String, String> attrs) {
        final XmlElement tmp = new XmlElement(this.current, name, attrs);

        if (this.current != null) this.current.appendChild(tmp);
        else this.root = tmp;

        this.current = tmp;
    }
    public void endNode(final boolean selfClosing) {
        this.current.setSelfClosing(selfClosing);
        this.current = this.current.parent;
    }
    public void someText(final String txt) {
        if (txt == null) return;

        this.current.children.add(new XmlTextElement(this.current, txt));
    }
    public XmlElement getRoot() {
        return this.root;
    }
}
