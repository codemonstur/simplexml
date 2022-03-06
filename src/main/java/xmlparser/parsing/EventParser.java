package xmlparser.parsing;

import java.util.Map;

public interface EventParser {

    void startNode(String name, Map<String, String> attrs);
    void endNode(boolean selfClosing);
    void someText(String txt);

}

