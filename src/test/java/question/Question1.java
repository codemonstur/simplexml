package question;

import xmlparser.XmlParser;
import xmlparser.annotations.XmlAttribute;
import xmlparser.annotations.XmlName;

/**
 * Example taken from:
 * https://stackoverflow.com/questions/51997363/convert-xml-containing-both-schema-and-data-into-java-object-using-jackson
 */
public class Question1 {

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
            "                <StudentId>&&&&&&&</StudentId>\n" +
            "                <FName>ABC</FName>\n" +
            "                <LName>DSF</LName>\n" +
            "                <Address1>12345</Address1>\n" +
            "            </StudentRecord>\n" +
            "        </DocumentElement>\n" +
            "    </diffgr:diffgram>\n" +
            "</DataTable>";

    public class StudentRecord {
        @XmlAttribute
        @XmlName("diffgr:id")
        public String id;
        @XmlAttribute
        @XmlName("msdata:rowOrder")
        public int rowOrder;
        @XmlName("StudentId")
        public String studentId;
        @XmlName("FName")
        public String firstName;
        @XmlName("LName")
        public String lastName;
        @XmlName("Address1")
        public String address1;
    }

    public static void main(final String... args) {
        final XmlParser parser = new XmlParser();
        final StudentRecord student = parser.fromXml(xml, "DataTable/diffgr:diffgram/DocumentElement/StudentRecord", StudentRecord.class);
        System.out.println(student.id + " : " + student.firstName + " " + student.lastName);
    }
}
