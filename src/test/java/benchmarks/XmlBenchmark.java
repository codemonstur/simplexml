package benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.parsers.*;
import javax.xml.stream.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import org.dom4j.io.SAXReader;

import org.jdom2.input.SAXBuilder;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonNode;

import xmlparser.XmlParser;
import xmlparser.model.XmlElement;

/**
 * Compares parsing a moderately sized XML document across several popular Java XML parsers.
 *
 * Run with: mvn -q exec:java -Dexec.mainClass=org.openjdk.jmh.Main -Dexec.args="benchmarks.XmlBenchmark"
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(0)
public class XmlBenchmark {

    private static final int ELEMENTS = 2000;

    private String xml;
    private XmlParser simpleXmlParser;

    @Setup
    public void setup() {
        final StringBuilder sb = new StringBuilder(1 << 16);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<catalog>\n");
        for (int i = 0; i < ELEMENTS; i++) {
            sb.append("  <book id=\"").append(i).append("\" category=\"fiction\">\n");
            sb.append("    <title>Book ").append(i).append("</title>\n");
            sb.append("    <author>Author ").append(i % 50).append("</author>\n");
            sb.append("    <price currency=\"USD\">").append(9.99 + (i % 10)).append("</price>\n");
            sb.append("    <description>A short description for book ").append(i).append(".</description>\n");
            sb.append("  </book>\n");
        }
        sb.append("</catalog>\n");
        xml = sb.toString();
        simpleXmlParser = new XmlParser();
    }

    // ---- SimpleXml ----
    @Benchmark
    public void simpleXml(Blackhole bh) throws Exception {
        final XmlElement root = simpleXmlParser.fromXml(xml);
        bh.consume(countSimpleXml(root));
    }

    private static int countSimpleXml(XmlElement e) {
        if (e == null || e.children == null) return 0;
        int n = 1;
        for (final XmlElement c : e.children) n += countSimpleXml(c);
        return n;
    }

    // ---- JDK DOM ----
    @Benchmark
    public void jdkDom(Blackhole bh) throws Exception {
        final DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        final DocumentBuilder b = f.newDocumentBuilder();
        final Document doc = b.parse(new InputSource(new StringReader(xml)));
        bh.consume(countDom(doc.getDocumentElement()));
    }

    private static int countDom(Node n) {
        if (n == null) return 0;
        int c = 1;
        final NodeList nl = n.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) c += countDom(nl.item(i));
        return c;
    }

    // ---- JDK SAX ----
    @Benchmark
    public void jdkSax(Blackhole bh) throws Exception {
        final SAXParserFactory f = SAXParserFactory.newInstance();
        final SAXParser p = f.newSAXParser();
        final int[] count = {0};
        final DefaultHandler h = new DefaultHandler() {
            public void startElement(String uri, String local, String qName, Attributes attrs) { count[0]++; }
        };
        p.parse(new InputSource(new StringReader(xml)), h);
        bh.consume(count[0]);
    }

    // ---- JDK StAX ----
    @Benchmark
    public void jdkStax(Blackhole bh) throws Exception {
        final XMLInputFactory f = XMLInputFactory.newInstance();
        final XMLEventReader r = f.createXMLEventReader(new StringReader(xml));
        int count = 0;
        while (r.hasNext()) {
            if (r.nextEvent().isStartElement()) count++;
        }
        r.close();
        bh.consume(count);
    }

    // ---- DOM4J ----
    @Benchmark
    public void dom4j(Blackhole bh) throws Exception {
        final SAXReader r = new SAXReader();
        final org.dom4j.Document d = r.read(new StringReader(xml));
        final org.dom4j.Element root = d.getRootElement();
        int n = root.elements().size();
        for (final Object o : root.elements()) {
            n += ((org.dom4j.Element) o).elements().size();
        }
        bh.consume(n);
    }

    // ---- JDOM2 ----
    @Benchmark
    public void jdom2(Blackhole bh) throws Exception {
        final SAXBuilder b = new SAXBuilder();
        final org.jdom2.Document d = b.build(new StringReader(xml));
        bh.consume(d.getRootElement().getChildren().size());
    }

    // ---- Jackson XML ----
    @Benchmark
    public void jacksonXml(Blackhole bh) throws Exception {
        final XmlMapper m = new XmlMapper();
        final JsonNode n = m.readTree(xml);
        bh.consume(n.size());
    }

    // ---- Woodstox (StAX) ----
    @Benchmark
    public void woodstoxStax(Blackhole bh) throws Exception {
        final XMLInputFactory f = (XMLInputFactory) Class.forName("com.ctc.wstx.stax.WstxInputFactory").getDeclaredConstructor().newInstance();
        final XMLEventReader r = f.createXMLEventReader(new StringReader(xml));
        int count = 0;
        while (r.hasNext()) {
            if (r.nextEvent().isStartElement()) count++;
        }
        r.close();
        bh.consume(count);
    }
}
