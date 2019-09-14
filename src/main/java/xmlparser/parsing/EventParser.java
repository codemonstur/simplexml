package xmlparser.parsing;

import java.util.Map;

public interface EventParser {

    void startNode(final String name, final Map<String, String> attrs);
    void endNode();
    void someText(final String txt);

}

