package benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import xmlparser.XmlParser;

/**
 * Object serialization / deserialization benchmarks.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(0)
public class ObjectBenchmark {

    private BenchmarkPojo pojo;
    private String simpleXmlString;
    private String jacksonString;
    private XmlParser simpleXmlParser;
    private XmlMapper jacksonMapper;

    @Setup
    public void setup() throws Exception {
        pojo = BenchmarkPojo.sample();
        simpleXmlParser = new XmlParser();
        simpleXmlString = simpleXmlParser.toXml(pojo);
        jacksonMapper = new XmlMapper();
        jacksonString = jacksonMapper.writeValueAsString(pojo);
    }

    // ---- SimpleXml serialize ----
    @Benchmark
    public void simpleXmlSerialize(Blackhole bh) {
        bh.consume(simpleXmlParser.toXml(pojo));
    }

    // ---- SimpleXml deserialize ----
    @Benchmark
    public void simpleXmlDeserialize(Blackhole bh) throws Exception {
        bh.consume(simpleXmlParser.fromXml(simpleXmlString, BenchmarkPojo.class));
    }

    // ---- Jackson serialize ----
    @Benchmark
    public void jacksonSerialize(Blackhole bh) throws Exception {
        bh.consume(jacksonMapper.writeValueAsString(pojo));
    }

    // ---- Jackson deserialize ----
    @Benchmark
    public void jacksonDeserialize(Blackhole bh) throws Exception {
        bh.consume(jacksonMapper.readValue(jacksonString, BenchmarkPojo.class));
    }
}
