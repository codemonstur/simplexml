package xmlparser;

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

public final class XmlParser {
    private final XmlCompress compress;
    private final XmlReader reader;
    private final XmlWriter writer;
    private final XmlIterator stream;
    private final boolean conserveWhitespace;
    private final Trim trimmer;
    private final UnEscape unescaper;
    private final Charset charset;

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

    public String compressXml(final String xml) {
        return compress.compressXml(xml, trimmer);
    }

    public <T> T fromXml(final Path xmlFile, final Class<T> clazz) throws IOException {
        try (final var in = newInputStream(xmlFile)) {
            return fromXml(in, clazz);
        }
    }
    public <T> T fromXml(final String xml, final Class<T> clazz) {
        return fromXml(fromXml(xml), clazz);
    }
    public <T> T fromXml(final InputStream xmlStream, final Class<T> clazz) throws IOException {
        return fromXml(fromXml(xmlStream), clazz);
    }
    public <T> T fromXml(final XmlElement element, final Class<T> clazz) {
        return reader.domToObject(element, clazz);
    }
    public <T> T fromXml(final String xml, final String xpath, final Class<T> clazz) {
        return fromXml(xml, newXPath(xpath), clazz);
    }
    public <T> T fromXml(final String xml, final XPathExpression xpath, final Class<T> clazz) {
        return fromXml(xpath.evaluateAny(fromXml(xml)), clazz);
    }

    public XmlElement fromXml(final Path xmlFile) throws IOException {
        try (final var in = newInputStream(xmlFile)) {
            return toXmlDom(newStreamReader(in, charset), conserveWhitespace, trimmer, unescaper);
        }
    }

    public XmlElement fromXml(final String input) {
        try {
            return toXmlDom(newStreamReader(input, charset), conserveWhitespace, trimmer, unescaper);
        } catch (final IOException ignore) { return null; }
    }
    public XmlElement fromXml(final InputStream in) throws IOException {
        return toXmlDom(newStreamReader(in, charset), conserveWhitespace, trimmer, unescaper);
    }
    public String toXml(final Object o) {
        return writer.toXml(o);
    }
    public void toXml(final Object o, final Writer out) throws IOException {
        writer.toXml(o, out);
    }
    public String domToXml(final XmlElement node) {
        return writer.domToXml(node);
    }
    public void domToXml(final XmlElement node, final Writer out) throws IOException {
        writer.domToXml(node, out);
    }
    public CheckedIterator<String> iterateXml(final String input) {
        return stream.iterateXml(newStreamReader(input, charset));
    }
    public CheckedIterator<String> iterateXml(final InputStream input) {
        return stream.iterateXml(newStreamReader(input, charset));
    }
    public CheckedIterator<XmlElement> iterateDom(final String input) {
        return stream.iterateDom(newStreamReader(input, charset), charset, conserveWhitespace, trimmer, unescaper);
    }
    public CheckedIterator<XmlElement> iterateDom(final InputStream input) {
        return stream.iterateDom(newStreamReader(input, charset), charset, conserveWhitespace, trimmer, unescaper);
    }
    public <T> CheckedIterator<T> iterateObject(final InputStream input, final Class<T> clazz) {
        return stream.iterateObject(newStreamReader(input, charset), charset, reader, clazz, conserveWhitespace, trimmer, unescaper);
    }

    public static Builder newXmlParser() {
        return new Builder();
    }

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

        public Builder defaultSerializer(final ObjectSerializer serializer) {
            this.defaultSerializer = serializer;
            return this;
        }
        public Builder addSerializer(final Class<?> c, final ObjectSerializer serializer) {
            this.serializers.put(c, serializer);
            return this;
        }
        public Builder addDeserializer(final Class<?> c, final ObjectDeserializer deserializer) {
            this.deserializers.put(c, deserializer);
            return this;
        }
        public Builder windowsNewLines() {
            this.newLine = CARRIAGE_RETURN + LINE_FEED;
            return this;
        }
        public Builder conserveWhitespace() {
            this.conserveWhitespace = true;
            return this;
        }
        public Builder trimmer(final Trim trimmer) {
            this.trimmer = trimmer;
            return this;
        }
        public Builder lenientWhitespace() {
            this.trimmer = new Trimming.LenientTrimmer();
            return this;
        }
        public Builder strictWhitespace() {
            this.trimmer = new Trimming.XmlTrimmer();
            return this;
        }
        public Builder escapeXml() {
            this.escaper = Escaping::escapeXml;
            return this;
        }
        public Builder escapeHtml() {
            this.escaper = Escaping::escapeHtml;
            return this;
        }
        public Builder unescapeXml() {
            this.unescaper = Escaping::unescapeXml;
            return this;
        }
        public Builder unescapeHtml() {
            this.unescaper = Escaping::unescapeHtml;
            return this;
        }
        public Builder escape(final Escape escaper) {
            this.escaper = escaper;
            return this;
        }
        public Builder unescape(final UnEscape unescaper) {
            this.unescaper = unescaper;
            return this;
        }
        public Builder supportHtml() {
            this.escaper = Escaping::escapeHtml;
            this.unescaper = Escaping::unescapeHtml;
            return this;
        }
        public Builder shouldPrettyPrint() {
            this.shouldPrettyPrint = true;
            return this;
        }
        public Builder shouldPrettyPrint(final boolean shouldPrettyPrint) {
            this.shouldPrettyPrint = shouldPrettyPrint;
            return this;
        }
        public Builder shouldEncodeUTF8() {
            this.shouldEncodeUTF8 = true;
            return this;
        }
        public Builder shouldEncodeUTF8(final boolean shouldEncodeUTF8) {
            this.shouldEncodeUTF8 = shouldEncodeUTF8;
            return this;
        }
        public Builder enableEnumCaching() {
            this.enableEnumCaching = true;
            return this;
        }
        public Builder disableEnumCaching() {
            this.enableEnumCaching = false;
            return this;
        }
        public Builder enumCaching(final boolean enumCaching) {
            this.enableEnumCaching = enumCaching;
            return this;
        }
        public Builder charset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public XmlParser build() {
            return new XmlParser(shouldEncodeUTF8, shouldPrettyPrint, conserveWhitespace, enableEnumCaching,
                    charset, newLine, defaultSerializer, serializers, deserializers, trimmer, escaper, unescaper);
        }
    }

}
