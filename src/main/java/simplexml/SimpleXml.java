package simplexml;

import simplexml.model.ElementNode;
import simplexml.model.ObjectDeserializer;
import simplexml.model.ObjectSerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static simplexml.XmlReader.parseXML;
import static simplexml.model.ObjectDeserializer.defaultDeserializers;
import static simplexml.model.ObjectSerializer.defaultSerializer;
import static simplexml.utils.Reflection.toName;

public final class SimpleXml {
    private final XmlReader reader;
    private final XmlWriter writer;

    public SimpleXml() {
        this(false, true, defaultSerializer(), new HashMap<>(), defaultDeserializers());
    }

    private SimpleXml(final boolean shouldEncodeUTF8, final boolean shouldPrettyPrint, final ObjectSerializer defaultSerializer
            , final Map<Class<?>, ObjectSerializer> serializers, final Map<Class<?>, ObjectDeserializer> deserializers) {
        reader = deserializers::get;
        writer = new XmlWriter() {
            public boolean hasSerializer(final Class<?> type) {
                return serializers.containsKey(type);
            }
            public ObjectSerializer getSerializer(final Class<?> type) {
                return serializers.getOrDefault(type, defaultSerializer);
            }
            public boolean shouldEncodeUTF8() {
                return shouldEncodeUTF8;
            }
            public boolean shouldPrettyPrint() {
                return shouldPrettyPrint;
            }
        };
    }

    public <T> T fromXml(final String input, final Class<T> clazz) throws IOException {
        return reader.domToObject(fromXml(input), clazz);
    }
    public <T> T fromXml(final InputStream in, final Class<T> clazz) throws IOException {
        return reader.domToObject(fromXml(in), clazz);
    }
    public ElementNode fromXml(final String input) throws IOException {
        return fromXml(new ByteArrayInputStream(input.getBytes(UTF_8)));
    }
    public ElementNode fromXml(final InputStream stream) throws IOException {
        return parseXML(new InputStreamReader(stream, UTF_8));
    }
    public String toXml(final Object o) {
        return writer.toXml(o, toName(o.getClass()));
    }
    public String toXml(final Object o, final String name) {
        return writer.toXml(o, name);
    }
    public void toXml(final Object o, final Writer out) throws IOException {
        writer.toXml(o, toName(o.getClass()), out);
    }
    public void toXml(final Object o, final String name, final Writer out) throws IOException {
        writer.toXml(o, name, out);
    }
    public String domToXml(final ElementNode node) {
        return writer.domToXml(node);
    }
    public void domToXml(final ElementNode node, final Writer out) throws IOException {
        writer.domToXml(node, out);
    }

    public static Builder newSimpleXml() {
        return new Builder();
    }

    public static class Builder {
        private boolean shouldEncodeUTF8 = false;
        private boolean shouldPrettyPrint = true;
        private ObjectSerializer defaultSerializer = ObjectSerializer.defaultSerializer();;
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

        public SimpleXml build() {
            return new SimpleXml(shouldEncodeUTF8, shouldPrettyPrint, defaultSerializer, serializers, deserializers);
        }
    }
}
