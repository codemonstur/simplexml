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

## Success criteria

- Measurable improvement in JMH throughput for `XmlBenchmark` and `ObjectBenchmark`.
- No correctness regressions in the unit test suite.
- Reduced allocation rate in hot serialization/deserialization paths.
