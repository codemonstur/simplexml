package xmlparser;

import xmlparser.annotations.*;
import xmlparser.error.InvalidAnnotation;
import xmlparser.error.InvalidObject;
import xmlparser.error.InvalidXPath;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.parsing.DomBuilder;
import xmlparser.parsing.ObjectDeserializer;
import xmlparser.utils.AccessDeserializers;
import xmlparser.utils.Escaping.UnEscape;
import xmlparser.utils.Trimming.Trim;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

import sun.reflect.ReflectionFactory;

import static xmlparser.model.XmlElement.findChildForName;
import static xmlparser.utils.Functions.isNullOrEmpty;
import static xmlparser.utils.Reflection.*;
import static xmlparser.utils.Validator.multipleAreNotNull;
import static xmlparser.xpath.XPathExpression.newXPath;

/**
 * The reading half of the mapper: fills an object, a record, a collection or a map from a DOM
 * tree, honouring the annotations found on the way and running any validators the type declares.
 * It is an interface so that XmlParser can hand it its caches; reading a document usually goes
 * through XmlParser rather than through here.
 */
public interface XmlReader extends AccessDeserializers {

    /**
     * Fills an object of the given type from a DOM element. A deserializer registered for the type
     * is used as it stands; otherwise a record is built through its canonical constructor and any
     * other class is allocated without running a constructor, and the validators the type declares
     * are run over the result. A null node gives null.
     *
     * @param <T> the type of object to fill
     * @param node the element to read from, possibly null
     * @param clazz the type to build
     * @return the filled object, or null when the node is null
     * @throws InvalidXPath if a {@link XmlPath} on the type does not evaluate against the node
     */
    default <T> T domToObject(final XmlElement node, final Class<T> clazz) throws InvalidXPath {
        if (node == null) return null;
        final ObjectDeserializer c = getDeserializer(clazz);
        if (c != null) return c.convert(node, clazz);

        final T object = clazz.isRecord() ? domToRecord(node, clazz) : domToClass(node, clazz);
        runValidators(object, clazz);

        return object;
    }

    /**
     * Runs every method annotated with {@link XmlObjectValidator}. The whole hierarchy is searched
     * because getDeclaredMethods() would only see what the class declares itself, so a validator
     * on a superclass or an implemented interface would otherwise never run. Each signature is
     * collected once so that an overriding method is not counted twice.
     */
    private void runValidators(final Object object, final Class<?> clazz) {
        final Map<String, Method> validators = new LinkedHashMap<>();
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
            collectValidators(c, validators);

        for (final Method method : validators.values()) {
            try {
                method.setAccessible(true);
                method.invoke(object);
            } catch (final Exception e) {
                throw new InvalidObject(clazz, e);
            }
        }
    }

    private void collectValidators(final Class<?> type, final Map<String, Method> validators) {
        for (final Method method : type.getDeclaredMethods())
            if (method.isAnnotationPresent(XmlObjectValidator.class))
                validators.putIfAbsent(method.getName() + Arrays.toString(method.getParameterTypes()), method);
        for (final Class<?> iface : type.getInterfaces()) collectValidators(iface, validators);
    }

    private <T> T domToRecord(final XmlElement node, final Class<T> clazz) {
        final String parentName = toName(clazz);
        XmlElement selectedNode;

        final RecordComponent[] fields = clazz.getRecordComponents();
        final Object[] fieldValues = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            final RecordComponent f = fields[i];
            if (f.isAnnotationPresent(XmlNoImport.class)) continue;

            selectedNode = node;
            if (f.isAnnotationPresent(XmlPath.class)) {
                selectedNode = newXPath(parentName + "/" + f.getAnnotation(XmlPath.class).value()).evaluateAny(node);
            }

            final Object fieldValue = switch (toFieldType(f)) {
                case FIELD_DESERIALIZER -> invokeFieldDeserializer(f, selectedNode);
                case TEXT_NODE -> textNodeToValue(toPattern(f), f.getType(), selectedNode);
                case ANNOTATED_ATTRIBUTE -> attributeToValue(toPattern(f), f.getType(), toName(f), deWrap(selectedNode, f));
                case SET -> domToSet(f, toClassOfCollection(f), toName(f), deWrap(selectedNode, f));
                case LIST -> domToList(f, toClassOfCollection(f), toName(f), deWrap(selectedNode, f));
                case ARRAY -> domToArray(f.getType().getComponentType(), toName(f), deWrap(selectedNode, f));
                case MAP -> domToMap(f, (ParameterizedType) f.getGenericType(), toName(f), deWrap(selectedNode, f));
                case ENUM -> enumNodeToValue(toEnumType(f), toName(f), deWrap(selectedNode, f));
                default -> {
                    final String name = toName(f);
                    if (selectedNode == null) yield null;

                    final String value = selectedNode.attributes.get(name);
                    if (value != null) {
                        yield stringToValue(f.getType(), value);
                    }
                    final var isAbstract = f.getAnnotation(XmlAbstractClass.class);
                    if (isAbstract != null) {
                        final XmlElement child = selectedNode.findChildForName(name, null);
                        yield domToObject(child, findAbstractType(isAbstract, child));
                    }

                    final var childForName = findChildForName(deWrap(selectedNode, f), name, null);
                    if (childForName != null) yield domToObject(childForName, f.getType());

                    final var hasDefault = f.getAnnotation(XmlDefaultValue.class);
                    if (hasDefault != null && hasDefault.deserialize()) {
                        yield stringToValue(f.getType(), hasDefault.value());
                    }

                    yield null;
                }
            };
            fieldValues[i] = fieldValue;
        }

        // A primitive component can never be null, so anything the document didn't provide (missing
        // element, @XmlNoImport, @XmlPath pointing at nothing) falls back to the zero value
        for (int i = 0; i < fields.length; i++) {
            if (fieldValues[i] == null) fieldValues[i] = zeroValueOf(fields[i].getType());
        }

        try {
            return canonicalConstructorOfRecord(clazz).newInstance(fieldValues);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SecurityException("Canonical constructor of record could not be invoked", e);
        }
    }

    private <T> T domToClass(final XmlElement node, final Class<T> clazz) {
        final T o = newInstanceWithoutConstructor(clazz);

        final String parentName = toName(clazz);
        XmlElement selectedNode;
        for (final Field f : listFields(clazz)) {
            f.setAccessible(true);
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (f.isAnnotationPresent(XmlNoImport.class)) continue;

            selectedNode = node;
            if (f.isAnnotationPresent(XmlPath.class)) {
                selectedNode = newXPath(parentName + "/" + f.getAnnotation(XmlPath.class).value()).evaluateAny(node);
            }

            final Object fieldValue = switch (toFieldType(f)) {
                case FIELD_DESERIALIZER -> invokeFieldDeserializer(f, selectedNode);
                case TEXT_NODE -> textNodeToValue(toPattern(f), f.getType(), selectedNode);
                case ANNOTATED_ATTRIBUTE -> attributeToValue(toPattern(f), f.getType(), toName(f), deWrap(selectedNode, f));
                case SET -> domToSet(f, toClassOfCollection(f), toName(f), deWrap(selectedNode, f));
                case LIST -> domToList(f, toClassOfCollection(f), toName(f), deWrap(selectedNode, f));
                case ARRAY -> domToArray(f.getType().getComponentType(), toName(f), deWrap(selectedNode, f));
                case MAP -> domToMap(f, (ParameterizedType) f.getGenericType(), toName(f), deWrap(selectedNode, f));
                case ENUM -> enumNodeToValue(toEnumType(f), toName(f), deWrap(selectedNode, f));
                default -> {
                    final String name = toName(f);
                    if (selectedNode == null) yield null;

                    final String value = selectedNode.attributes.get(name);
                    if (value != null) {
                        yield stringToValue(f.getType(), value);
                    }
                    final var isAbstract = f.getAnnotation(XmlAbstractClass.class);
                    if (isAbstract != null) {
                        final XmlElement child = selectedNode.findChildForName(name, null);
                        yield domToObject(child, findAbstractType(isAbstract, child));
                    }
                    final var childForName = findChildForName(deWrap(selectedNode, f), name, null);
                    if (childForName != null) yield domToObject(childForName, f.getType());

                    final var hasDefault = f.getAnnotation(XmlDefaultValue.class);
                    if (hasDefault != null && hasDefault.deserialize()) {
                        yield stringToValue(f.getType(), hasDefault.value());
                    }

                    yield null;
                }
            };
            setField(f, o, fieldValue == null ? zeroValueOf(f.getType()) : fieldValue);
        }
        return o;
    }

    private XmlElement deWrap(final XmlElement element, final Field field) {
        if (!isWrapped(field) || element == null) return element;
        return element.findChildForName(toWrappedName(field), null);
    }
    private XmlElement deWrap(final XmlElement element, final RecordComponent field) {
        if (!isWrapped(field) || element == null) return element;
        return element.findChildForName(toWrappedName(field), null);
    }
    private Object textNodeToValue(final String pattern, final Class<?> type, final XmlElement node) {
        final ObjectDeserializer conv = getDeserializer(type);
        final Object value = (conv != null) ? conv.convert(node) : null;
        if (!isNullOrEmpty(pattern) && value != null)
            validatePattern(pattern, value);
        return value;
    }
    private Object enumNodeToValue(final Class<? extends Enum> type, final String name, final XmlElement node) {
        final XmlElement text = findChildForName(node, name, null);
        if (text == null) return null;
        final String value = text.getText();
        if (value == null) return null;
        final ObjectDeserializer conv = getDeserializer(type);
        return (conv == null) ? valueOfEnum(type, value) : conv.convert(node);
    }

    /**
     * Tells the enum lookups whether their results may be kept between reads. An implementer
     * supplies the flag; XmlParser takes it from its builder.
     *
     * @return true when the two caches below are to be used, false to rebuild every time
     */
    boolean isEnumCachingEnabled();

    /**
     * The cache an implementer supplies of enum constants by their document name, one map per
     * enum type, filled in on demand when caching is enabled.
     *
     * @param <T> the enum type being looked up
     * @return the cache to look a constant up in by name
     */
    <T extends Enum> Map<Class<T>, Map<String, T>> getEnumCache();

    /**
     * The cache an implementer supplies of the constant marked with XmlEnumDefaultValue, one per
     * enum type, filled in on demand when caching is enabled. It backs the fallback for a document
     * value that matches no constant, and stays empty for a type that declares no default.
     *
     * @param <T> the enum type being looked up
     * @return the cache to look the default constant up in
     */
    <T extends Enum> Map<Class<T>, T> getEnumDefaultCache();

    private <T extends Enum> Map<String, T> getEnumValueDirectory(final Class<T> type) {
        final boolean enumCaching = isEnumCachingEnabled();
        if (enumCaching) {
            final Map<Class<T>, Map<String, T>> cache = getEnumCache();
            return cache.computeIfAbsent(type, this::newEnumValueMap);
        }
        return newEnumValueMap(type);
    }
    private <T extends Enum> T getDefaultValueForEnum(final Class<T> type) {
        final boolean enumCaching = isEnumCachingEnabled();
        if (enumCaching) {
            final Map<Class<T>, T> cache = getEnumDefaultCache();
            return cache.computeIfAbsent(type, this::newEnumDefault);
        }
        return newEnumDefault(type);
    }

    private <T extends Enum> Map<String, T> newEnumValueMap(final Class<T> type) {
        final Map<String, T> valueMap = new HashMap<>();
        for (final T t : type.getEnumConstants()) {
            try {
                final XmlEnumValue annotation = type.getField(t.name()).getAnnotation(XmlEnumValue.class);
                valueMap.put(annotation != null ? annotation.value() : t.name(), t);
            } catch (final NoSuchFieldException e) {
                // impossible
                throw new RuntimeException(e);
            }
        }
        return valueMap;
    }
    private <T extends Enum> T newEnumDefault(final Class<T> type) {
        for (final T t : type.getEnumConstants()) {
            try {
                if (type.getField(t.name()).isAnnotationPresent(XmlEnumDefaultValue.class))
                    return t;
            } catch (final NoSuchFieldException e) {
                // impossible
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private <T extends Enum> T valueOfEnum(final Class<T> type, final String value) {
        final T ret1 = getEnumValueDirectory(type).get(value);
        if (ret1 != null) return ret1;

        final T ret2 = getDefaultValueForEnum(type);
        if (ret2 != null) return ret2;

        throw new IllegalArgumentException("No enum constant for " + type.getName() + "." + value);
    }

    private Object attributeToValue(final String pattern, final Class<?> type, final String name, final XmlElement node) {
        if (node == null) return null;
        final ObjectDeserializer conv = getDeserializer(type);
        if (conv == null) return null;
        final String value = node.attributes.get(name);
        if (value == null) return null;
        if (!isNullOrEmpty(pattern))
            validatePattern(pattern, value);
        return conv.convert(value);
    }
    private Object stringToValue(final Class<?> type, final String value) {
        final ObjectDeserializer conv = getDeserializer(type);
        return (conv != null) ? conv.convert(value) : null;
    }
    @SuppressWarnings("unchecked")
    private static Class<? extends Enum> toEnumClass(final Class<?> type) {
        return (Class<? extends Enum>) type;
    }

    /**
     * Builds a map key or map value that only exists as text: a tag name, an attribute value or
     * the text of an element. Types with a deserializer use it and enums are looked up by name,
     * but any other type can not be built from text alone.
     */
    private Object toMapTextValue(final ObjectDeserializer conv, final Class<?> type, final String text) {
        if (text == null) return null;
        if (conv != null) return conv.convert(text);
        return type.isEnum() ? valueOfEnum(toEnumClass(type), text) : null;
    }
    private Set<Object> domToSet(final RecordComponent field, final Class<?> type, final String name, final XmlElement node) throws InvalidXPath {
        return node == null ? null : domToSet(field.getAnnotation(XmlAbstractClass.class), field.getType(), type, name, node);
    }
    private Set<Object> domToSet(final Field field, final Class<?> type, final String name, final XmlElement node) throws InvalidXPath {
        return node == null ? null : domToSet(field.getAnnotation(XmlAbstractClass.class), field.getType(), type, name, node);
    }
    private Set<Object> domToSet(final XmlAbstractClass isAbstract, final Class<?> declaredType, final Class<?> type
            , final String name, final XmlElement node) throws InvalidXPath {
        final ObjectDeserializer elementConv = getDeserializer(type);

        final Set<Object> set = newSet(declaredType);
        for (final XmlElement n : node.children) {
            if (!n.name.equals(name)) continue;
            if (isAbstract != null) {
                set.add(domToObject(n, findAbstractType(isAbstract, n)));
                continue;
            }

            set.add( (elementConv == null) ? domToObject(n, type) : elementConv.convert(n));
        }
        return set;
    }

    private List<Object> domToList(final RecordComponent field, final Class<?> type, final String name, final XmlElement node) throws InvalidXPath {
        return node == null ? null : domToList(field.getAnnotation(XmlAbstractClass.class), field.getType(), type, name, node);
    }
    private List<Object> domToList(final Field field, final Class<?> type, final String name, final XmlElement node) throws InvalidXPath {
        return node == null ? null : domToList(field.getAnnotation(XmlAbstractClass.class), field.getType(), type, name, node);
    }
    private List<Object> domToList(final XmlAbstractClass isAbstract, final Class<?> declaredType, final Class<?> type
            , final String name, final XmlElement node) throws InvalidXPath {
        if (node == null) return null;
        final ObjectDeserializer elementConv = getDeserializer(type);

        final List<Object> list = newList(declaredType);
        for (final XmlElement n : node.children) {
            if (!n.name.equals(name)) continue;
            if (isAbstract != null) {
                list.add(domToObject(n, findAbstractType(isAbstract, n)));
                continue;
            }

            list.add( (elementConv == null) ? domToObject(n, type) : elementConv.convert(n));
        }
        return list;
    }
    private Object domToArray(final Class<?> type, final String name, final XmlElement node) throws InvalidXPath {
        if (node == null) return null;
        final ObjectDeserializer elementConv = getDeserializer(type);

        final Object array = Array.newInstance(type, node.numChildrenWithName(name));
        int i = 0;
        for (final XmlElement n : node.children) {
            if (n.name.equals(name)) {
                Array.set(array, i, (elementConv == null) ? domToObject(n, type) : elementConv.convert(n, type));
                i++;
            }
        }
        return array;
    }
    private Map<Object, Object> domToMap(final RecordComponent field, final ParameterizedType type, final String name, final XmlElement node) {
        if (node == null) return null;

        final XmlMapTagIsKey isXmlMapTagIsKey = field.getAnnotation(XmlMapTagIsKey.class);
        final XmlMapWithAttributes isXmlMapWithAttributes = field.getAnnotation(XmlMapWithAttributes.class);
        final XmlMapWithChildNodes isXmlMapWithChildNodes = field.getAnnotation(XmlMapWithChildNodes.class);

        return domToMap(isXmlMapTagIsKey, isXmlMapWithAttributes, isXmlMapWithChildNodes, field.getType(), type, name, node);
    }
    private Map<Object, Object> domToMap(final Field field, final ParameterizedType type, final String name, final XmlElement node) {
        if (node == null) return null;

        final XmlMapTagIsKey isXmlMapTagIsKey = field.getAnnotation(XmlMapTagIsKey.class);
        final XmlMapWithAttributes isXmlMapWithAttributes = field.getAnnotation(XmlMapWithAttributes.class);
        final XmlMapWithChildNodes isXmlMapWithChildNodes = field.getAnnotation(XmlMapWithChildNodes.class);

        return domToMap(isXmlMapTagIsKey, isXmlMapWithAttributes, isXmlMapWithChildNodes, field.getType(), type, name, node);
    }

    private Map<Object, Object> domToMap(final XmlMapTagIsKey isXmlMapTagIsKey, final XmlMapWithAttributes isXmlMapWithAttributes
            , final XmlMapWithChildNodes isXmlMapWithChildNodes, final Class<?> declaredType, final ParameterizedType type
            , final String name, final XmlElement node) {
        if (multipleAreNotNull(isXmlMapTagIsKey, isXmlMapWithAttributes, isXmlMapWithChildNodes))
            throw new InvalidAnnotation("Only one of XmlMapTagIsKey, XmlMapWithAttributes and XmlMapWithChildNodes is allowed per field");

        final Class<?> keyType = toClassOfMapKey(type);
        final Class<?> valueType = toClassOfMapValue(type);
        final ObjectDeserializer convKey = getDeserializer(keyType);
        final ObjectDeserializer convVal = getDeserializer(valueType);

        if (isXmlMapWithAttributes != null) {
            final String keyName = isXmlMapWithAttributes.keyName();
            final String valueName = isXmlMapWithAttributes.valueName();

            final Map<Object, Object> map = newMap(declaredType);
            for (final XmlElement child : node.children) {
                if (!name.equals(child.name)) continue;

                // a key that is only an attribute value can only be built by a deserializer or an enum
                final Object mapKey = toMapTextValue(convKey, keyType, child.attributes.get(keyName));
                if (mapKey == null) continue;

                final String value = valueName.isEmpty() ? child.getText() : child.attributes.get(valueName);
                // A missing value is a null entry, which is how a null map value is serialized
                map.put(mapKey, value == null ? null : toMapTextValue(convVal, valueType, value));
            }
            return map;
        }
        if (isXmlMapWithChildNodes != null) {
            final String keyName = isXmlMapWithChildNodes.keyName();
            final String valueName = isXmlMapWithChildNodes.valueName();

            final Map<Object, Object> map = newMap(declaredType);
            for (final XmlElement child : node.children) {
                if (!name.equals(child.name)) continue;

                final XmlElement key = child.findChildForName(keyName, null);
                if (key == null) continue;
                final String keyText = key.getText();

                final XmlElement value = valueName.isEmpty() ? child : child.findChildForName(valueName, null);
                if (value == null) continue;
                final String valueText = value.getText();

                // a key or value of a type without a deserializer is built from its element
                final Object mapKey = (convKey != null) ? (keyText == null ? null : convKey.convert(keyText))
                        : (keyType.isEnum() && keyText != null ? valueOfEnum(toEnumClass(keyType), keyText)
                                : domToObject(key, keyType));
                if (mapKey == null) continue;

                final Object mapValue = (convVal != null) ? (valueText == null ? null : convVal.convert(valueText))
                        : (valueType.isEnum() && valueText != null ? valueOfEnum(toEnumClass(valueType), valueText)
                                : domToObject(value, valueType));
                map.put(mapKey, mapValue);
            }
            return map;
        }

        final XmlElement element = node.findChildForName(name, null);
        if (element == null) return null;

        final Map<Object, Object> map = newMap(declaredType);
        for (final XmlElement child : element.children) {
            // a key that is only a tag name can only be built by a deserializer or an enum
            final Object mapKey = toMapTextValue(convKey, keyType, child.name);
            if (mapKey == null) continue;

            final Object mapValue = (convVal != null) ? convVal.convert(child)
                    : (valueType.isEnum() ? valueOfEnum(toEnumClass(valueType), child.getText())
                            : domToObject(child, valueType));
            map.put(mapKey, mapValue);
        }
        return map;
    }

    /**
     * Fields can be declared as an interface (List, Set, Map) or as a concrete implementation
     * (ArrayList, TreeSet, ...). When they are declared as a concrete implementation we have to
     * build that exact type, otherwise the assignment into the field would fail.
     */
    @SuppressWarnings("unchecked")
    private List<Object> newList(final Class<?> declaredType) {
        return isConcrete(declaredType) ? (List<Object>) newInstanceOf(declaredType) : new LinkedList<>();
    }
    @SuppressWarnings("unchecked")
    private Set<Object> newSet(final Class<?> declaredType) {
        return isConcrete(declaredType) ? (Set<Object>) newInstanceOf(declaredType) : new HashSet<>();
    }
    @SuppressWarnings("unchecked")
    private Map<Object, Object> newMap(final Class<?> declaredType) {
        return isConcrete(declaredType) ? (Map<Object, Object>) newInstanceOf(declaredType) : new HashMap<>();
    }
    private static boolean isConcrete(final Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    }

    /**
     * Builds an object of the given class without running any of its constructors, which is what
     * lets a class be deserialized whether or not it holds a no-arg constructor. The allocation is
     * inherited from Object, so every field comes back at its default and a constructor body, if
     * there is one at all, never runs. The fields are filled in afterwards.
     */
    @SuppressWarnings("unchecked")
    private static <T> T newInstanceWithoutConstructor(final Class<T> clazz) {
        try {
            final Constructor<?> allocation = ReflectionFactory.getReflectionFactory()
                    .newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor());
            allocation.setAccessible(true);
            return (T) allocation.newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new InvalidAnnotation("Class " + clazz.getName() + " could not be instantiated", e);
        }
    }
    // Collections are expected to be in a valid state, so unlike the classes above they are built
    // through their constructor instead of being handed out empty
    private static Object newInstanceOf(final Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new InvalidAnnotation("Collection type " + type.getName() + " must have a no-arg constructor", e);
        }
    }

    /**
     * Hands back the compiled form of a pattern, compiled on the first call for it and reused for
     * every later one, so a pattern repeated across many reads is only built once.
     *
     * @param pattern the regular expression to compile
     * @return the compiled pattern, one shared form per pattern text
     */
    Pattern internPattern(String pattern);

    private void validatePattern(final String pattern, final Object value) {
        final Pattern p = internPattern(pattern);
        if (!p.matcher(value.toString()).matches())
            throw new InvalidXml("Pattern match failure for pattern '" + pattern + "'");
    }

    /**
     * Reads a whole document off the stream and builds the DOM tree at its root. Unbalanced tags,
     * or a document holding no element at all, are an error rather than a partial tree.
     *
     * @param in the stream to read the document from
     * @param conserveWhitespace whether whitespace in text is kept instead of trimmed
     * @param trimmer what counts as whitespace, and how it is stripped from text
     * @param escaper how entities in text and attributes are unescaped
     * @return the root element of the built tree
     * @throws IOException if the stream cannot be read
     */
    static XmlElement toXmlDom(final InputStreamReader in, final boolean conserveWhitespace,
                               final Trim trimmer, final UnEscape escaper) throws IOException {
        final DomBuilder p = new DomBuilder();
        XmlStreamReader.toXmlStream(in, p, conserveWhitespace, trimmer, escaper);
        if (!p.isCurrentNull()) throw new InvalidXml("Unbalanced tags");
        if (p.isRootNull()) throw new InvalidXml("No XML tags found");
        return p.getRoot();
    }

}
