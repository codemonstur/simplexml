package xmlparser;

import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.parsing.ObjectDeserializer;
import xmlparser.parsing.ObjectSerializer;
import xmlparser.utils.CheckedIterator;
import xmlparser.utils.Escaping;
import xmlparser.utils.Escaping.Escape;
import xmlparser.utils.Escaping.UnEscape;
import xmlparser.utils.Trimming;
import xmlparser.utils.Trimming.Trim;
import xmlparser.xpath.XPathExpression;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import static xmlparser.XmlReader.toXmlDom;
import static xmlparser.parsing.ObjectDeserializer.defaultDeserializers;
import static xmlparser.parsing.ObjectSerializer.defaultSerializer;
import static xmlparser.utils.Constants.CARRIAGE_RETURN;
import static xmlparser.utils.Constants.LINE_FEED;
import static xmlparser.utils.IO.newStreamReader;
import static xmlparser.utils.Trimming.NativeTrimmer;
import static xmlparser.xpath.XPathExpression.newXPath;

/**
 * The front door of the library. Reads XML into a DOM tree or straight into objects, writes
 * objects and DOM trees back out, streams a long document one element at a time, and compresses
 * it. The no-arg constructor gives a parser with sensible defaults, and {@link Builder} is for
 * everything else.
 */
public final class XmlParser {
    private final XmlCompress compress;
    private final XmlReader reader;
    private final XmlWriter writer;
    private final XmlIterator stream;
    private final boolean conserveWhitespace;
    private final Trim trimmer;
    private final UnEscape unescaper;
    private final Charset charset;

    /**
     * Creates a parser with the defaults: UTF-8, pretty printing on, whitespace between tags
     * dropped, enum caching on, line feed line endings, XML escaping and unescaping, the JDK's
     * own idea of whitespace, and the serializers and deserializers the library ships with.
     */
    public XmlParser() {
        this(false, true, false, true, UTF_8, LINE_FEED,
            defaultSerializer(), new HashMap<>(), defaultDeserializers(), new NativeTrimmer(), Escaping::escapeXml,
            Escaping::unescapeXml);
    }

    private XmlParser(final boolean shouldEncodeUTF8, final boolean shouldPrettyPrint, final boolean conserveWhitespace,
                      final boolean enableEnumCaching, final Charset charset, final String newLine,
                      final ObjectSerializer defaultSerializer, final Map<Class<?>, ObjectSerializer> serializers,
                      final Map<Class<?>, ObjectDeserializer> deserializers, final Trim trimmer, final Escape escaper,
                      final UnEscape unescaper) {
        this.charset = charset;
        this.compress = new XmlCompress() {};
        final var enumCache = new HashMap<Class<Enum>, Map<String, Enum>>();
        final var enumDefaultCache = new HashMap<Class<Enum>, Enum>();
        final var patternMap = new HashMap<String, Pattern>();
        this.reader = new XmlReader() {
            public boolean isEnumCachingEnabled() {
                return enableEnumCaching;
            }
            public Pattern internPattern(final String pattern) {
                return patternMap.computeIfAbsent(pattern, Pattern::compile);
            }
            public Map<Class<Enum>, Map<String, Enum>> getEnumCache() {
                return enumCache;
            }
            public Map<Class<Enum>, Enum> getEnumDefaultCache() {
                return enumDefaultCache;
            }
            public ObjectDeserializer getDeserializer(final Class<?> type) {
                return deserializers.get(type);
            }
        };
        this.writer = new XmlWriter() {
            public String escape(final String input) {
                return escaper.escape(input, shouldEncodeUTF8);
            }
            public String newLine() {
                return newLine;
            }
            public boolean hasSerializer(final Class<?> type) {
                return serializers.containsKey(type);
            }
            public ObjectSerializer getSerializer(final Class<?> type) {
                return serializers.getOrDefault(type, defaultSerializer);
            }
            public boolean shouldPrettyPrint() {
                return shouldPrettyPrint;
            }
        };
        this.stream = new XmlIterator() {};
        this.conserveWhitespace = conserveWhitespace;
        this.trimmer = trimmer;
        this.unescaper = unescaper;
    }

    /**
     * Compresses a document for a machine: the whitespace pretty printing put between tags is
     * dropped, text nodes are trimmed, comments and prologs are left out, and a CDATA section is
     * copied through untouched because its body is literal.
     *
     * @param xml the document to compress
     * @return the document with everything the reader does not need taken out
     * @throws InvalidXml if the input cannot be read or holds a tag that is never closed
     */
    public String compressXml(final String xml) {
        return compress.compressXml(xml, trimmer);
    }

    /**
     * Reads the file and fills an object of the given type from it, honouring the annotations
     * and validators the type declares.
     *
     * @param <T> the type to fill
     * @param xmlFile the file to read
     * @param clazz the type to fill
     * @return an instance of the type, built from the document
     * @throws IOException if the file cannot be read
     */
    public <T> T fromXml(final Path xmlFile, final Class<T> clazz) throws IOException {
        try (final var in = newInputStream(xmlFile)) {
            return fromXml(in, clazz);
        }
    }
    /**
     * Fills an object of the given type from the document held in the string, honouring the
     * annotations and validators the type declares.
     *
     * @param <T> the type to fill
     * @param xml the document to read
     * @param clazz the type to fill
     * @return an instance of the type, built from the document
     * @throws InvalidXml if the document is not well formed
     */
    public <T> T fromXml(final String xml, final Class<T> clazz) {
        return fromXml(fromXml(xml), clazz);
    }
    /**
     * Reads the stream and fills an object of the given type from it. The stream is read but not
     * closed.
     *
     * @param <T> the type to fill
     * @param xmlStream the stream to read
     * @param clazz the type to fill
     * @return an instance of the type, built from the document
     * @throws IOException if the stream cannot be read
     */
    public <T> T fromXml(final InputStream xmlStream, final Class<T> clazz) throws IOException {
        return fromXml(fromXml(xmlStream), clazz);
    }
    /**
     * Fills an object of the given type from a DOM tree read earlier, with the same annotations,
     * deserializers and validators as a document read from scratch.
     *
     * @param <T> the type to fill
     * @param element the element to read from
     * @param clazz the type to fill
     * @return an instance of the type, or null when the element is null
     */
    public <T> T fromXml(final XmlElement element, final Class<T> clazz) {
        return reader.domToObject(element, clazz);
    }
    /**
     * Walks the document down to the node the xpath names and fills an object of the given type
     * from whatever is found there.
     *
     * @param <T> the type to fill
     * @param xml the document to read
     * @param xpath the path to walk, parsed as {@link XPathExpression#newXPath(String)} would
     * @param clazz the type to fill
     * @return an instance of the type, or null when the path matches nothing
     */
    public <T> T fromXml(final String xml, final String xpath, final Class<T> clazz) {
        return fromXml(xml, newXPath(xpath), clazz);
    }
    /**
     * Evaluates the expression against the document and fills an object of the given type from
     * the first node it matches.
     *
     * @param <T> the type to fill
     * @param xml the document to read
     * @param xpath the expression to evaluate
     * @param clazz the type to fill
     * @return an instance of the type, or null when the expression matches nothing
     */
    public <T> T fromXml(final String xml, final XPathExpression xpath, final Class<T> clazz) {
        return fromXml(xpath.evaluateAny(fromXml(xml)), clazz);
    }

    /**
     * Reads the file into a DOM tree.
     *
     * @param xmlFile the file to read
     * @return the root element of the tree
     * @throws IOException if the file cannot be read
     * @throws InvalidXml if the document is not well formed or holds no tags
     */
    public XmlElement fromXml(final Path xmlFile) throws IOException {
        try (final var in = newInputStream(xmlFile)) {
            return toXmlDom(newStreamReader(in, charset), conserveWhitespace, trimmer, unescaper);
        }
    }

    /**
     * Reads the string into a DOM tree. The string is encoded and decoded with the charset the
     * parser was built with, and a read failure is wrapped in InvalidXml rather than escaping.
     *
     * @param input the document to read
     * @return the root element of the tree
     * @throws InvalidXml if the document is not well formed or holds no tags
     */
    public XmlElement fromXml(final String input) {
        try {
            return toXmlDom(newStreamReader(input, charset), conserveWhitespace, trimmer, unescaper);
        } catch (final IOException e) {
            throw new InvalidXml("Could not read the XML", e);
        }
    }
    /**
     * Reads the stream into a DOM tree. The stream is read but not closed.
     *
     * @param in the stream to read
     * @return the root element of the tree
     * @throws IOException if the stream cannot be read
     * @throws InvalidXml if the document is not well formed or holds no tags
     */
    public XmlElement fromXml(final InputStream in) throws IOException {
        return toXmlDom(newStreamReader(in, charset), conserveWhitespace, trimmer, unescaper);
    }
    /**
     * Reflects over the object and writes it as XML, escaped and pretty printed the way the
     * parser was built. A DOM element is written as itself.
     *
     * @param o the object to write
     * @return the document
     * @throws InvalidXml if the fields of the object cannot be read
     */
    public String toXml(final Object o) {
        return writer.toXml(o);
    }
    /**
     * Reflects over the object and writes it to the writer instead of holding it in a string.
     *
     * @param o the object to write
     * @param out the writer to write it to
     * @throws IOException if writing fails
     * @throws InvalidXml if the fields of the object cannot be read
     */
    public void toXml(final Object o, final Writer out) throws IOException {
        writer.toXml(o, out);
    }
    /**
     * Writes a DOM tree back out, escaped and pretty printed the way the parser was built.
     *
     * @param node the element to write
     * @return the document
     * @throws InvalidXml if the tree cannot be written
     */
    public String domToXml(final XmlElement node) {
        return writer.domToXml(node);
    }
    /**
     * Writes a DOM tree to the writer instead of holding it in a string.
     *
     * @param node the element to write
     * @param out the writer to write it to
     * @throws IOException if writing fails
     */
    public void domToXml(final XmlElement node, final Writer out) throws IOException {
        writer.domToXml(node, out);
    }
    /**
     * Walks the document one top level element at a time, giving each back as XML, without
     * reading ahead: a long document can be walked while only one element is in memory.
     *
     * @param input the document to walk
     * @return an iterator over the elements, which throws when the input is not well formed
     */
    public CheckedIterator<String> iterateXml(final String input) {
        return stream.iterateXml(newStreamReader(input, charset));
    }
    /**
     * Walks the stream one top level element at a time, giving each back as XML, without reading
     * ahead. The stream is read as the iterator goes and is never closed.
     *
     * @param input the document to walk
     * @return an iterator over the elements, which throws when the input is not well formed
     */
    public CheckedIterator<String> iterateXml(final InputStream input) {
        return stream.iterateXml(newStreamReader(input, charset));
    }
    /**
     * Walks the string one top level element at a time, each read into a DOM tree of its own,
     * without reading ahead.
     *
     * @param input the document to walk
     * @return an iterator over the elements, which throws when the input is not well formed
     */
    public CheckedIterator<XmlElement> iterateDom(final String input) {
        return stream.iterateDom(newStreamReader(input, charset), charset, conserveWhitespace, trimmer, unescaper);
    }
    /**
     * Walks the stream one top level element at a time, each read into a DOM tree of its own,
     * without reading ahead. The stream is read as the iterator goes and is never closed.
     *
     * @param input the document to walk
     * @return an iterator over the elements, which throws when the input is not well formed
     */
    public CheckedIterator<XmlElement> iterateDom(final InputStream input) {
        return stream.iterateDom(newStreamReader(input, charset), charset, conserveWhitespace, trimmer, unescaper);
    }
    /**
     * Walks the stream one top level element at a time, each filled into an object of the given
     * type, without reading ahead.
     *
     * @param <T> the type to fill
     * @param input the document to walk
     * @param clazz the type to fill
     * @return an iterator over the objects, which throws when the input is not well formed
     */
    public <T> CheckedIterator<T> iterateObject(final InputStream input, final Class<T> clazz) {
        return stream.iterateObject(newStreamReader(input, charset), charset, reader, clazz, conserveWhitespace, trimmer, unescaper);
    }

    /**
     * Starts a parser that is told what to change rather than taking the defaults.
     *
     * @return a builder holding the defaults the no-arg constructor would give
     */
    public static Builder newXmlParser() {
        return new Builder();
    }

    /**
     * Gathers the settings a {@link XmlParser} is built with: how to escape and unescape text, when
     * to trim whitespace, whether to pretty print, which charset to read with, and any serializers
     * and deserializers of your own. Every setting returns this so the calls chain, and
     * {@link #build()} makes the parser.
     */
    public static class Builder {
        private boolean shouldEncodeUTF8 = false;
        private boolean shouldPrettyPrint = true;
        private boolean conserveWhitespace = false;
        private boolean enableEnumCaching = true;
        private Charset charset = UTF_8;
        private String newLine = LINE_FEED;
        private Trim trimmer = new NativeTrimmer();
        private Escape escaper = Escaping::escapeXml;
        private UnEscape unescaper = Escaping::unescapeXml;
        private ObjectSerializer defaultSerializer = ObjectSerializer.defaultSerializer();
        private Map<Class<?>, ObjectSerializer> serializers = new HashMap<>();
        private Map<Class<?>, ObjectDeserializer> deserializers = defaultDeserializers();

        /**
         * Starts a builder holding the defaults the no-arg {@link XmlParser} gets.
         */
        public Builder() { }

        /**
         * Sets the serializer asked for any type that has none of its own. The default writes the
         * value's own toString.
         *
         * @param serializer the fallback serializer
         * @return this builder
         */
        public Builder defaultSerializer(final ObjectSerializer serializer) {
            this.defaultSerializer = serializer;
            return this;
        }
        /**
         * Registers a serializer for one type, which is asked for that type instead of the
         * default serializer. No type has one of its own to begin with.
         *
         * @param c the type the serializer handles
         * @param serializer how to write values of that type
         * @return this builder
         */
        public Builder addSerializer(final Class<?> c, final ObjectSerializer serializer) {
            this.serializers.put(c, serializer);
            return this;
        }
        /**
         * Registers a deserializer for one type, replacing the one the library ships with when it
         * covers that type.
         *
         * @param c the type the deserializer handles
         * @param deserializer how to read values of that type
         * @return this builder
         */
        public Builder addDeserializer(final Class<?> c, final ObjectDeserializer deserializer) {
            this.deserializers.put(c, deserializer);
            return this;
        }
        /**
         * Writes a line break as a carriage return followed by a line feed. A line feed alone is
         * the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder windowsNewLines() {
            this.newLine = CARRIAGE_RETURN + LINE_FEED;
            return this;
        }
        /**
         * Keeps the whitespace between tags as it was written instead of dropping it. Dropping it
         * is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder conserveWhitespace() {
            this.conserveWhitespace = true;
            return this;
        }
        /**
         * Sets what counts as whitespace and how it is stripped from the ends of text. The JDK's
         * own idea of it is the default.
         *
         * @param trimmer the trimmer to read with
         * @return this builder
         */
        public Builder trimmer(final Trim trimmer) {
            this.trimmer = trimmer;
            return this;
        }
        /**
         * Reads with the lenient trimmer, which counts every character that has any business
         * looking like whitespace as whitespace. The JDK's own idea of it is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder lenientWhitespace() {
            this.trimmer = new Trimming.LenientTrimmer();
            return this;
        }
        /**
         * Reads with the strict trimmer, which counts as whitespace only the four characters the
         * XML standard allows. The JDK's own idea of it is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder strictWhitespace() {
            this.trimmer = new Trimming.XmlTrimmer();
            return this;
        }
        /**
         * Escapes text as XML when writing: the five characters that would otherwise end the tag
         * they are written in. This is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder escapeXml() {
            this.escaper = Escaping::escapeXml;
            return this;
        }
        /**
         * Escapes text with the named HTML entities rather than the five XML ones. Escaping as
         * XML is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder escapeHtml() {
            this.escaper = Escaping::escapeHtml;
            return this;
        }
        /**
         * Reads references back as XML when reading: the five named ones and numeric references.
         * This is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder unescapeXml() {
            this.unescaper = Escaping::unescapeXml;
            return this;
        }
        /**
         * Reads references back with the full set of named HTML entities rather than the five XML
         * ones. Unescaping as XML is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder unescapeHtml() {
            this.unescaper = Escaping::unescapeHtml;
            return this;
        }
        /**
         * Sets how text is escaped when writing, for a way of escaping that the XML and HTML
         * choices do not offer.
         *
         * @param escaper the escape to write with
         * @return this builder
         */
        public Builder escape(final Escape escaper) {
            this.escaper = escaper;
            return this;
        }
        /**
         * Sets how references are read back when reading, for a way of unescaping that the XML
         * and HTML choices do not offer.
         *
         * @param unescaper the unescape to read with
         * @return this builder
         */
        public Builder unescape(final UnEscape unescaper) {
            this.unescaper = unescaper;
            return this;
        }
        /**
         * Escapes and unescapes with the named HTML entities, both halves at once. XML both ways
         * is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder supportHtml() {
            this.escaper = Escaping::escapeHtml;
            this.unescaper = Escaping::unescapeHtml;
            return this;
        }
        /**
         * Pretty prints, with indentation and a line break per tag. Pretty printing is the
         * default.
         *
         * @return this, so the calls can be chained
         */
        public Builder shouldPrettyPrint() {
            this.shouldPrettyPrint = true;
            return this;
        }
        /**
         * Turns pretty printing on or off; off writes the document without indentation or line
         * breaks. It is on by default.
         *
         * @param shouldPrettyPrint whether to pretty print
         * @return this builder
         */
        public Builder shouldPrettyPrint(final boolean shouldPrettyPrint) {
            this.shouldPrettyPrint = shouldPrettyPrint;
            return this;
        }
        /**
         * Writes every character above the basic ASCII range as a numeric reference. Leaving them
         * as they are is the default.
         *
         * @return this, so the calls can be chained
         */
        public Builder shouldEncodeUTF8() {
            this.shouldEncodeUTF8 = true;
            return this;
        }
        /**
         * Turns the writing of characters above the basic ASCII range as numeric references on or
         * off. It is off by default.
         *
         * @param shouldEncodeUTF8 whether to write them as numeric references
         * @return this builder
         */
        public Builder shouldEncodeUTF8(final boolean shouldEncodeUTF8) {
            this.shouldEncodeUTF8 = shouldEncodeUTF8;
            return this;
        }
        /**
         * Caches the enums that have been looked up so that the same one is not looked up twice.
         * Caching is on by default.
         *
         * @return this, so the calls can be chained
         */
        public Builder enableEnumCaching() {
            this.enableEnumCaching = true;
            return this;
        }
        /**
         * Looks an enum up every time instead of reusing the one already looked up. Caching is on
         * by default.
         *
         * @return this, so the calls can be chained
         */
        public Builder disableEnumCaching() {
            this.enableEnumCaching = false;
            return this;
        }
        /**
         * Turns the reuse of enums already looked up on or off. It is on by default.
         *
         * @param enumCaching whether to cache enums
         * @return this builder
         */
        public Builder enumCaching(final boolean enumCaching) {
            this.enableEnumCaching = enumCaching;
            return this;
        }
        /**
         * Sets the charset every document is read with and written with. UTF-8 is the default.
         *
         * @param charset the charset to read and write with
         * @return this builder
         */
        public Builder charset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        /**
         * Makes the parser out of everything this builder has been told.
         *
         * @return the new parser
         */
        public XmlParser build() {
            return new XmlParser(shouldEncodeUTF8, shouldPrettyPrint, conserveWhitespace, enableEnumCaching,
                    charset, newLine, defaultSerializer, serializers, deserializers, trimmer, escaper, unescaper);
        }
    }

}
