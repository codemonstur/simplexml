# Performance Plan

Ideas for making simplexml faster, based on the current implementation and benchmark suite.

## Profiling first

- Use the existing JMH benchmarks in `src/test/benchmarks/` (`XmlBenchmark`, `ObjectBenchmark`) to identify whether the bottleneck is parsing, reflection, escaping, allocation, or collection handling.
- Add micro-benchmarks for representative workloads: large documents, deeply nested objects, maps/lists, enums, and repeated serialize/deserialize of the same types.

## High-impact optimizations

### 1. Cache reflection metadata

- Cache field lists, annotations, constructors, record components, and field classifications per `Class`.
- Use a `ConcurrentHashMap<Class<?>, Metadata>` so repeated operations on the same types avoid repeated introspection.
- Invalidate or version caches carefully if classloaders or dynamic classes are involved.

### 2. Avoid repeated regex and string splitting

- Reuse compiled `Pattern` instances; extend the existing `internPattern` approach to XPath and other hot paths.
- Avoid `String.split` in loops; scan characters directly where possible.

### 3. Reduce allocations

- Reuse `StringBuilder` and char buffers for parsing and serialization.
- Pre-size collections and builders based on known or estimated sizes.
- Avoid creating intermediate strings for every token during XML parsing.

### 4. Fast paths for common types

- Special-case `String`, primitives, enums, arrays, lists, maps, and records before falling back to generic reflection.
- Cache enum value mappings (`@XmlEnumValue`) instead of looking them up per instance.

### 5. Collection and lookup efficiency

- Avoid repeated `contains`/lookup operations in hot loops.
- Prefer direct field access and cached method handles where reflection overhead dominates.

### 6. Reuse and thread safety

- Keep parsers thread-safe while allowing thread-local scratch buffers.
- Reuse cached metadata across parser instances where safe.

## Suggested order

1. Profile current benchmarks.
2. Implement reflection metadata caching.
3. Re-run benchmarks and inspect allocation/GC behavior.
4. Add fast paths for primitives and enums.
5. Optimize parsing/serialization buffers.
6. Re-run benchmarks and adjust.

## Jackson-speed roadmap

These options are larger and more invasive than the high-impact optimizations above. They target the architectural gap between a reflection-and-DOM based parser and a streaming, generated-binding parser such as Jackson. Treat each as a separate experiment and profile it before committing.

### 1. Stream instead of building a DOM first

- Add a streaming XML reader (pull/SAX-style events) that drives object deserialization directly, without first materializing an `XmlElement` tree.
- Serialize by walking the object graph and writing fields directly to a `Writer`/output stream, rather than building or converting a DOM.
- Keep the existing DOM APIs for callers that need them, but let the object path bypass DOM construction and DOM-to-object conversion.
- Reuse the existing `XmlIterator` streaming infrastructure as the starting point, extending it from top-level elements to full object binding.

### 2. Generated or compiled binding code

- Generate per-type serializers and deserializers instead of using reflection on every call.
- Start with runtime method handles or `LambdaMetafactory` for field access, then evaluate runtime bytecode generation or an annotation processor for true compiled binders.
- Cache generated binders keyed by type and parser configuration; fall back to reflection for dynamic, anonymous, or unsupported types.
- Separate generated hot paths from cold setup/validation so class generation does not dominate startup.

### 3. Deep metadata caching

- Cache complete binding plans per type: field order, XML names, attributes, text nodes, wrappers, validators, converters, and nested type plans.
- Cache XPath/path-to-field mappings and resolved deserializers/serializers.
- Make cached plans immutable and share them across compatible parser instances.
- Version or invalidate plans when classloaders or dynamic classes are involved.

### 4. Further allocation reduction

- Write directly to reusable or pooled `StringBuilder`/char buffers and output writers instead of returning many intermediate strings.
- Parse text into existing buffers and avoid per-token/per-field temporary strings where correctness allows.
- Pre-size collections, maps, arrays, and builders from cached metadata or observed counts.
- Reduce boxing and temporary collections in list/map/array binding.

### 5. Specialized fast paths

- Add hand-optimized paths for primitives, `String`, enums, arrays, lists, maps, records, and common collection implementations.
- Add a fast path for simple beans with no attributes, XPath expressions, validators, or custom serializers.
- Prefer final/sealed/known types and monomorphic call sites so the JIT can inline and optimize.

### 6. Optimized escaping and text handling

- Escape and unescape with table-driven or branch-friendly scans, writing directly to output buffers.
- Avoid regex in entity and text handling; scan runs of safe characters and copy them in bulk.
- Consider caching escaped forms only for repeated values and only where memory use remains bounded.

### 7. JIT-friendly design

- Keep hot parse/bind methods small and focused; move cold validation and error reporting out of the hot path.
- Avoid excessive polymorphism and megamorphic dispatch in inner loops.
- Use stable, final types and cached call sites where possible.
- Benchmark in forked JVMs with representative data, measuring steady-state throughput and allocation rate rather than startup.

### Suggested adoption order for this track

1. Profile `ObjectBenchmark` to confirm whether parsing, binding, escaping, or allocation dominates.
2. Add a streaming object parse/serialize path while retaining DOM support.
3. Introduce cached binding plans and deeper metadata caches.
4. Replace reflection field access with method handles or generated accessors.
5. Optimize escaping and buffer reuse.
6. Evaluate full runtime code generation if method-handle paths are insufficient.
7. Re-run JMH and allocation profiling after each step.

## Success criteria

- Measurable improvement in JMH throughput for `XmlBenchmark` and `ObjectBenchmark`.
- No correctness regressions in the unit test suite.
- Reduced allocation rate in hot serialization/deserialization paths.
- For the Jackson-speed track, a measurable reduction in the throughput and allocation gap against Jackson on representative `ObjectBenchmark` workloads, without changing public API behavior.
"
