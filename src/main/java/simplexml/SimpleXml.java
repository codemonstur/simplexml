package simplexml;

import simplexml.model.ObjectDeserializer;
import simplexml.model.ObjectSerializer;

import java.util.HashMap;
import java.util.Map;

import static simplexml.model.ObjectDeserializer.defaultDeserializers;

public class SimpleXml implements XmlReader, XmlWriter {
    private final boolean shouldEncodeUTF8;
    private final ObjectSerializer defaultSerializer;
    private final Map<Class<?>, ObjectSerializer> serializers;
    private final Map<Class<?>, ObjectDeserializer> deserializers;

    public SimpleXml() {
        shouldEncodeUTF8 = false;
        deserializers = defaultDeserializers();
        defaultSerializer = ObjectSerializer.defaultSerializer();
        serializers = new HashMap<>();
    }

    private SimpleXml(final boolean shouldEncodeUTF8, final ObjectSerializer defaultSerializer,
                      final Map<Class<?>, ObjectSerializer> serializers, final Map<Class<?>, ObjectDeserializer> deserializers) {
        this.shouldEncodeUTF8 = shouldEncodeUTF8;
        this.deserializers = deserializers;
        this.serializers = serializers;
        this.defaultSerializer = defaultSerializer;
    }

    public boolean hasSerializer(final Class<?> type) {
        return serializers.containsKey(type);
    }
    public ObjectSerializer getSerializer(Class<?> type) {
        return serializers.getOrDefault(type, defaultSerializer);
    }
    public ObjectDeserializer getDeserializer(Class<?> type) {
        return deserializers.get(type);
    }
    public boolean shouldEncodeUTF8() {
        return shouldEncodeUTF8;
    }

    public static Builder newSimpleXml() {
        return new Builder();
    }

    public static class Builder {
        private boolean shouldEncodeUTF8;
        private ObjectSerializer defaultSerializer;
        private Map<Class<?>, ObjectSerializer> serializers;
        private Map<Class<?>, ObjectDeserializer> deserializers;

        private Builder() {
            shouldEncodeUTF8 = false;
            deserializers = defaultDeserializers();
            defaultSerializer = ObjectSerializer.defaultSerializer();
            serializers = new HashMap<>();
        }

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
        public Builder shouldEncodeUTF8() {
            this.shouldEncodeUTF8 = true;
            return this;
        }
        public Builder shouldEncodeUTF8(final boolean shouldEncodeUTF8) {
            this.shouldEncodeUTF8 = shouldEncodeUTF8;
            return this;
        }

        public SimpleXml build() {
            return new SimpleXml(shouldEncodeUTF8, defaultSerializer, serializers, deserializers);
        }
    }
}
