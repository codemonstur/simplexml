# Benchmark Suite

JMH benchmarks comparing SimpleXml against several popular Java XML parsers:

- SimpleXml (`xmlparser.XmlParser`)
- JDK DOM (`javax.xml.parsers.DocumentBuilder`)
- JDK SAX (`javax.xml.parsers.SAXParser`)
- JDK StAX (`javax.xml.stream`)
- DOM4J
- JDOM2
- Jackson XML (`XmlMapper`)
- Woodstox (StAX implementation)

Two benchmark classes:

- `XmlBenchmark` — parses a moderately sized XML document (~2000 `<book>` elements) and
  performs a small traversal to prevent dead-code elimination, across DOM/SAX/StAX-based
  parsers.
- `ObjectBenchmark` — object serialization and deserialization (SimpleXml vs. Jackson XML)
  using a shared `BenchmarkPojo` (nested object, list, primitive fields).

## Running

```bash
make benchmark
```

or directly:

```bash
mvn -q test-compile
mvn exec:java -Dexec.classpathScope=test
```

Results are printed to stdout. JMH runs 3 warmup iterations and 5 measurement
iterations per benchmark (1 second each) in a single fork.

## Notes

- JMH and the additional parsers are test-scoped dependencies, so they do not
  affect the published artifact.
- The benchmark uses `@Fork(0)` so it runs in the current JVM; for more rigorous
  results run with a forked JVM and a clean environment.
- The legacy text files `DirectVsIndirect.txt` and `InternalVsCustom.txt` are
  historical notes from ad-hoc trim benchmarks and are not part of this suite.
