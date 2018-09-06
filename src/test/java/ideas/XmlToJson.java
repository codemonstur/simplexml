package ideas;

import simplexml.XmlReader;
import simplexml.model.EventParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class XmlToJson {

    public static final String xml =
        "    <?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<DataTable xmlns=\"http://*****.com\">\n" +
        "    <xs:schema id=\"NewDataSet\" xmlns=\"\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\">\n" +
        "        <xs:element name=\"NewDataSet\" msdata:IsDataSet=\"true\" msdata:MainDataTable=\"StudentRecord\" msdata:UseCurrentLocale=\"true\">\n" +
        "            <xs:complexType>\n" +
        "                <xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
        "                    <xs:element name=\"StudentRecord\">\n" +
        "                        <xs:complexType>\n" +
        "                            <xs:sequence>\n" +
        "                                <xs:element name=\"StudentId\" type=\"xs:string\" minOccurs=\"0\" />\n" +
        "                                <xs:element name=\"FName\" type=\"xs:string\" minOccurs=\"0\" />\n" +
        "                                <xs:element name=\"LName\" type=\"xs:string\" minOccurs=\"0\" />\n" +
        "                                <xs:element name=\"Address1\" type=\"xs:string\" minOccurs=\"0\" />\n" +
        "                            </xs:sequence>\n" +
        "                        </xs:complexType>\n" +
        "                    </xs:element>\n" +
        "                </xs:choice>\n" +
        "            </xs:complexType>\n" +
        "        </xs:element>\n" +
        "    </xs:schema>\n" +
        "    <diffgr:diffgram xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\" xmlns:diffgr=\"urn:schemas-microsoft-com:xml-diffgram-v1\">\n" +
        "        <DocumentElement xmlns=\"\">\n" +
        "            <StudentRecord diffgr:id=\"StudentRecord1\" msdata:rowOrder=\"0\">\n" +
        "                <StudentId>&quot;</StudentId>\n" +
        "                <StudentLong />\n" +
        "                <FName>ABC</FName>\n" +
        "                <LName>DSF</LName>\n" +
        "                <Address1>12345</Address1>\n" +
        "            </StudentRecord>\n" +
        "        </DocumentElement>\n" +
        "    </diffgr:diffgram>\n" +
        "</DataTable>";

    public static void main(final String... args) throws IOException {
        // TODO support for arrays
        XmlReader.parseXML(new InputStreamReader(new ByteArrayInputStream(xml.getBytes(UTF_8))), new EventParser() {
            int indent = 0;
            String indentSpaces = "";
            boolean isRoot = true;
            boolean lastHadAttrs = false;
            boolean lastWasClose = false;
            boolean lastWasOpened = false;
            boolean valueWasWritten = false;
            public void startNode(final String name, final Map<String, String> attrs) {
                if (isRoot) {
                    isRoot = false;
                } else {
                    if (lastWasClose || lastHadAttrs) System.out.print(",\n");
                    else System.out.println("{");
                    System.out.print(indentSpaces+"\""+name + "\": ");
                }
                lastWasOpened = false;
                lastHadAttrs = false;
                valueWasWritten = false;
                indent++;
                indentSpaces = newIndent(indent);

                if (!attrs.isEmpty()) {
                    System.out.println("{");
                    int done = 0;
                    for (final Entry<String, String> entry : attrs.entrySet()) {
                        System.out.print(indentSpaces+"\""+entry.getKey()+"\": " + toPrintableValue(entry.getValue()));
                        if (done+1 != attrs.size()) System.out.println(",");
                        done++;
                    }
                    lastWasOpened = true;
                    valueWasWritten = true;
                }
                lastHadAttrs = !attrs.isEmpty();
                lastWasClose = false;
            }
            public void endNode() {
                if (!valueWasWritten) System.out.print("\"\"");
                if (lastWasClose || lastHadAttrs) System.out.println();

                indent--;
                indentSpaces = newIndent(indent);
                if (lastWasOpened) System.out.print(indentSpaces+"}");

                lastWasOpened = true;
                lastHadAttrs = false;
                lastWasClose = true;
                valueWasWritten = true;
            }
            public void someText(final String txt) {
                if (txt == null || txt.isEmpty()) return;

                if (!lastWasOpened) {
                    System.out.print(toPrintableValue(txt));
                    valueWasWritten = true;
                }
            }
        });
    }

    private static String newIndent(final int number) {
        String indent = "";
        for (int i = 0; i < number; i++) indent += "  ";
        return indent;
    }

    private static Pattern IS_NUMERIC = Pattern.compile("^[-+]?\\d+(\\.\\d+)?$");
    private static Pattern IS_BOOLEAN = Pattern.compile("^true|false$");
    private static String toPrintableValue(final String value) {
        if (IS_NUMERIC.matcher(value).matches()) return value;
        if (IS_BOOLEAN.matcher(value).matches()) return value;
        return escapeJson(value);
    }

    private static String escapeJson(final String string) {
        if (string == null || string.length() == 0) return "\"\"";

        final StringBuilder sb = new StringBuilder(string.length() + 4);
        sb.append('"');
        for (final char c : string.toCharArray()) {
            switch (c) {
                case '\\': case '"': case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        final String t = "000" + Integer.toHexString(c);
                        sb.append("\\u");
                        sb.append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
