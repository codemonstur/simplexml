package ideas;

import xmlparser.XmlStreamReader;
import xmlparser.parsing.EventParser;
import xmlparser.utils.Escaping;
import xmlparser.utils.IO;
import xmlparser.utils.Trimming;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static xmlparser.utils.IO.newStreamReader;

public class XmlToJson {

    public static final String xml =
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
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

    // Differences:
    // XML has namespaces, sortof part of the name, separator value is a colon which is not allowed in JSON
    // XML has child tags and attributes, JSON has only fields
    // XML has comments, JSON does not
    // JSON has arrays, XML has duplicate child objects
       // To write the object out correctly you would have to wait until all sibling tags have been read,
       // this rule is particularly nasty with respect to the root element, essentially you would not be
       // able to stream anything. The full JSON will be kept in memory until the whole thing can be written
    // JSON has type information (boolean, integer, string), XML has only strings
    // XML has self closing tags and regular tags, JSON has objects
    // XML has a named root tag, JSON does not
    // XML has text nodes, JSON does not

    public static void main(final String... args) throws IOException {
        // TODO support for arrays
        XmlStreamReader.toXmlStream(newStreamReader(xml, UTF_8), new EventParser() {
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
            public void endNode(final boolean selfClosing) {
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
        }, false, new Trimming.NativeTrimmer(), Escaping::unescapeXml);
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
