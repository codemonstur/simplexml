package xmlparser.utils;

import xmlparser.annotations.*;
import xmlparser.annotations.XmlAbstractClass.TypeMap;
import xmlparser.error.AssignmentFailure;
import xmlparser.error.InvalidAnnotation;
import xmlparser.model.XmlElement;

import java.lang.reflect.*;
import java.util.*;

import static java.lang.String.format;
import static xmlparser.model.XmlElement.findChildForName;
import static xmlparser.utils.Reflection.ClassType.*;

/**
 * A helper class that uses reflection to introspect and construct various objects and classes.
 */
public enum Reflection {;

    // I would like to define T as 'T extends Record', haven't figured out how to get the generics to
    // understand what type I have. The XmlReader calls this using a Class<?> that has been checked with
    // an if. But how to upcast after that if?

    /**
     * Returns the canonical constructor for a given record.
     * @param recordClass the record to find the constructor for
     * @return the constructor
     * @param <T> Any type really, but it does have to be a record
     * @throws SecurityException Really shouldn't happen
     * @throws NoSuchMethodException Really shouldn't happen
     */
    public static <T> Constructor<T> canonicalConstructorOfRecord(final Class<T> recordClass) throws SecurityException, NoSuchMethodException {
        final var componentTypes = Arrays.stream(recordClass.getRecordComponents())
            .map(RecordComponent::getType).toArray(Class<?>[]::new);
        return recordClass.getDeclaredConstructor(componentTypes);
    }

    /**
     * Loops through all the fields of clazz and figures out what kind they are. Populates the
     * attributes, and childnodes lists.
     * @param clazz the class to take the fields of
     * @param o the object to take the instances of
     * @param attributes the list of attributes for this class
     * @param childNodes the list of childnodes for this class
     * @return the textnode, if any
     * @throws IllegalAccessException Really shouldn't happen
     */
    public static Field determineTypeOfFields(final Class<?> clazz, final Object o, final List<Field> attributes
            , final List<Field> childNodes) throws IllegalAccessException {
        Field textNode = null;
        for (final Field f : listFields(clazz)) {
            f.setAccessible(true);
            final var hasDefault = f.getAnnotation(XmlDefaultValue.class);
            if (f.get(o) == null && (hasDefault == null || !hasDefault.serialize())) continue;
            if (Modifier.isStatic(f.getModifiers())) continue;
            if (Modifier.isTransient(f.getModifiers())) continue;
            if (f.isAnnotationPresent(XmlNoExport.class)) continue;
            if (f.isAnnotationPresent(XmlTextNode.class))
                textNode = f;
            else if (f.isAnnotationPresent(XmlAttribute.class))
                attributes.add(f);
            else
                childNodes.add(f);
        }
        return textNode;
    }

    /**
     * Looks up the value to put into XML for a given Enum instance. Usually just .name() but it
     * may also be mapped to something else using the @XmlEnumValue annotation.
     * @param o the enum instance to convert
     * @return the XML value
     */
    public static String toEnumValue(final Enum o) {
        try {
            final String name = o.name();
            final Field field = o.getClass().getField(name);
            final XmlEnumValue annotation = field.getAnnotation(XmlEnumValue.class);
            return annotation != null ? annotation.value() : name;
        } catch (final NoSuchFieldException e) {
            // impossible
            throw new RuntimeException(e);
        }
    }

    /**
     * Casts the declared type of an enum field to the enum class the rest of the code works with.
     * The cast is unchecked, so the caller has to have established that the field is an enum.
     * @param field the field to take the declared type of
     * @return the declared type, as an enum class
     */
    public static Class<? extends Enum> toEnumType(final Field field) {
        return (Class<? extends Enum>) field.getType();
    }
    /**
     * Casts the declared type of an enum record component to the enum class the rest of the code
     * works with. The cast is unchecked, so the caller has to have established it is an enum.
     * @param field the component to take the declared type of
     * @return the declared type, as an enum class
     */
    public static Class<? extends Enum> toEnumType(final RecordComponent field) {
        return (Class<? extends Enum>) field.getType();
    }

    /**
     * What a field is as far as the mapper is concerned: an attribute, a text node, a set, a list, an
     * array, a map, an enum, a field with a deserializer of its own, or an object with a tag to
     * itself. Both halves switch on this rather than asking the same questions twice.
     */
    public enum FieldType {
        /** The element's text content, as marked by @XmlTextNode. */
        TEXT_NODE,
        /** A value written as an attribute of the element. */
        ANNOTATED_ATTRIBUTE,
        /** A field declared as a Set. */
        SET,
        /** A field declared as a List. */
        LIST,
        /** A field declared as an array. */
        ARRAY,
        /** A field declared as a Map. */
        MAP,
        /** Anything else: an object with a tag of its own. */
        OTHER,
        /** Read by the function named in @XmlFieldDeserializer. */
        FIELD_DESERIALIZER,
        /** A field declared as an enum. */
        ENUM
    }
    /**
     * Classifies a field by its annotations first and by its declared type after, so both halves
     * of the mapper switch on one answer. A Collection that is neither a List nor a Set counts as
     * a List, as does the Iterable interface itself.
     * @param f the field to classify
     * @return the field's kind
     */
    public static FieldType toFieldType(final Field f) {
        if (f.isAnnotationPresent(XmlFieldDeserializer.class)) return FieldType.FIELD_DESERIALIZER;
        if (f.isAnnotationPresent(XmlTextNode.class)) return FieldType.TEXT_NODE;
        if (f.isAnnotationPresent(XmlAttribute.class)) return FieldType.ANNOTATED_ATTRIBUTE;

        final Class<?> type = f.getType();
        if (type.isEnum()) return FieldType.ENUM;
        if (Set.class.isAssignableFrom(type)) return FieldType.SET;
        if (List.class.isAssignableFrom(type)) return FieldType.LIST;
        if (type.equals(Iterable.class) || Collection.class.isAssignableFrom(type)) return FieldType.LIST;
        if (type.isArray()) return FieldType.ARRAY;
        if (Map.class.isAssignableFrom(type)) return FieldType.MAP;
        return FieldType.OTHER;
    }
    /**
     * Classifies a record component by its annotations first and by its declared type after, so
     * both halves of the mapper switch on one answer. A Collection that is neither a List nor a
     * Set counts as a List, as does the Iterable interface itself.
     * @param f the record component to classify
     * @return the component's kind
     */
    public static FieldType toFieldType(final RecordComponent f) {
        if (f.isAnnotationPresent(XmlFieldDeserializer.class)) return FieldType.FIELD_DESERIALIZER;
        if (f.isAnnotationPresent(XmlTextNode.class)) return FieldType.TEXT_NODE;
        if (f.isAnnotationPresent(XmlAttribute.class)) return FieldType.ANNOTATED_ATTRIBUTE;

        final Class<?> type = f.getType();
        if (type.isEnum()) return FieldType.ENUM;
        if (Set.class.isAssignableFrom(type)) return FieldType.SET;
        if (List.class.isAssignableFrom(type)) return FieldType.LIST;
        if (type.equals(Iterable.class) || Collection.class.isAssignableFrom(type)) return FieldType.LIST;
        if (type.isArray()) return FieldType.ARRAY;
        if (Map.class.isAssignableFrom(type)) return FieldType.MAP;
        return FieldType.OTHER;
    }

    /**
     * The same idea as FieldType but for a class rather than a field: a value of this type is written
     * as its own text, as a sequence of items, or as an element holding children of its own.
     */
    public enum ClassType {
        /** Written as its own text. */
        SIMPLE,
        /** Written as a sequence of elements. */
        ARRAY,
        /** Written as a sequence of items. */
        LIST,
        /** Written as a sequence of members. */
        SET,
        /** Written as a sequence of entries. */
        MAP,
        /** An element holding children of its own. */
        OBJECT,
        /** Written as the name of one of its constants. */
        ENUM
    }
    /**
     * The same classification for a class being written: enums first, then anything simple or
     * with a serializer of its own, then the container shapes, and finally an object that gets a
     * tag and children of its own.
     * @param c the class to classify
     * @param s the serializers the writer knows about, which can make a class simple
     * @return the class's kind
     */
    public static ClassType toClassType(final Class<?> c, final AccessSerializers s) {
        // a constant with a body of its own has a class of its own, which is not itself an enum
        if (c.isEnum() || isEnumConstantClass(c)) return ENUM;
        if (isSimple(c) || s.hasSerializer(c)) return SIMPLE;
        if (c.isArray()) return ARRAY;
        if (isList(c)) return LIST;
        if (isSet(c)) return SET;
        if (isMap(c)) return MAP;
        return OBJECT;
    }

    /**
     * Tells whether a class is the one a constant with a body of its own is compiled into: the
     * class itself is not an enum, but the class it extends is, and its instances are still
     * constants of that enum.
     * @param c the class to check
     * @return true for the anonymous class of an enum constant
     */
    private static boolean isEnumConstantClass(final Class<?> c) {
        return c.getSuperclass() != null && c.getSuperclass().isEnum();
    }

    /**
     * Collects the declared fields of a class and of every class it extends, including the static
     * and transient ones that callers filter out later. Synthetic fields, which the compiler adds
     * for things like the outer instance of an inner class, are left out: they are not part of the
     * object's data and would otherwise be written as an element of their own, or loop back into
     * the enclosing object forever.
     * @param type the class to take the fields of
     * @return the fields of the class and its superclasses, subclass first
     */
    public static List<Field> listFields(final Class<?> type) {
        return listFields(new ArrayList<>(), type);
    }
    /**
     * Appends the declared fields of a class and of every class it extends to the given list,
     * walking up the hierarchy one class at a time. Synthetic fields are skipped for the reason
     * {@link #listFields(Class)} gives, and so is a field whose name a class nearer the object's
     * own class has already declared: the nearer field hides the one it shadows, which would
     * otherwise be written under the same name and read back from the one element into both.
     * @param fields the list to add to, which already holds the fields of any subclass
     * @param type the class to take the fields of
     * @return the same list
     */
    public static List<Field> listFields(final List<Field> fields, final Class<?> type) {
        for (final Field f : type.getDeclaredFields()) {
            if (f.isSynthetic()) continue;
            fields.add(f);
        }
        if (type.getSuperclass() == null) return fields;

        final Set<String> names = new HashSet<>();
        for (final Field f : fields) names.add(f.getName());
        for (final Field inherited : listFields(new ArrayList<>(), type.getSuperclass()))
            if (names.add(inherited.getName())) fields.add(inherited);
        return fields;
    }

    /**
     * Tells whether a value of this type is written as one piece of text rather than as an
     * element: the numeric primitives, chars and booleans, their boxed forms, and String.
     * @param c the class to check
     * @return true for the types that serialize as plain text
     */
    public static boolean isSimple(final Class<?> c) {
        return c.equals(Double.class)
            || c.equals(double.class)
            || c.equals(Integer.class)
            || c.equals(String.class)
            || c.equals(int.class)
            || c.equals(float.class)
            || c.equals(Float.class)
            || c.equals(byte.class)
            || c.equals(Byte.class)
            || c.equals(char.class)
            || c.equals(Character.class)
            || c.equals(short.class)
            || c.equals(Short.class)
            || c.equals(Long.class)
            || c.equals(long.class)
            || c.equals(boolean.class)
            || c.equals(Boolean.class);
    }
    /**
     * These return true for the interface a field is declared as, as well as for every concrete
     * implementation. A Collection that is neither a Set nor a Map is treated as a List, as is
     * the Iterable interface itself, so that a Queue or any other Iterable is written as the
     * sequence of items it reads back as.
     * @param c the class to check
     * @return true for every Collection and Iterable that is not a Set or a Map
     */
    public static boolean isList(final Class<?> c) {
        if (Set.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c)) return false;
        return Collection.class.isAssignableFrom(c) || Iterable.class.isAssignableFrom(c);
    }
    /**
     * Tells whether values of this class are serialized as a set, which is the Set interface and
     * every class implementing it.
     * @param c the class to check
     * @return true for Set and its implementations
     */
    public static boolean isSet(final Class<?> c) {
        return Set.class.isAssignableFrom(c);
    }
    /**
     * Tells whether values of this class are serialized as a map, which is the Map interface and
     * every class implementing it.
     * @param c the class to check
     * @return true for Map and its implementations
     */
    public static boolean isMap(final Class<?> c) {
        return Map.class.isAssignableFrom(c);
    }
    /**
     * Tells whether the value is nested inside a wrapper tag, as asked for by @XmlWrapperTag.
     * @param f the field to check
     * @return true if the annotation is present
     */
    public static boolean isWrapped(final Field f) {
        return f.isAnnotationPresent(XmlWrapperTag.class);
    }
    /**
     * Tells whether the value is nested inside a wrapper tag, as asked for by @XmlWrapperTag.
     * @param f the record component to check
     * @return true if the annotation is present
     */
    public static boolean isWrapped(final RecordComponent f) {
        return f.isAnnotationPresent(XmlWrapperTag.class);
    }
    /**
     * The name of the wrapper tag the value is nested inside. Only worth asking of a field that
     * isWrapped says is wrapped; without the annotation this fails with a NullPointerException.
     * @param f the field to take the wrapper name of
     * @return the value of the @XmlWrapperTag annotation
     */
    public static String toWrappedName(final Field f) {
        return f.getAnnotation(XmlWrapperTag.class).value();
    }
    /**
     * The name of the wrapper tag the value is nested inside. Only worth asking of a component
     * that isWrapped says is wrapped; without the annotation this fails with a NullPointerException.
     * @param f the record component to take the wrapper name of
     * @return the value of the @XmlWrapperTag annotation
     */
    public static String toWrappedName(final RecordComponent f) {
        return f.getAnnotation(XmlWrapperTag.class).value();
    }
    /**
     * Resolves the concrete class an abstract field is built as. The type marker is read from a
     * child tag when the annotation names one, and from an attribute (default "class") when it
     * does not, and is then matched against the annotation's name to type pairs.
     * @param annotation the annotation naming the marker and the types it may stand for
     * @param node the element the marker is read out of
     * @return the class the marker stands for
     * @throws InvalidAnnotation if the marker is missing or matches no type
     */
    public static Class<?> findAbstractType(final XmlAbstractClass annotation, final XmlElement node) {
        final String typeName = findAbstractTypeName(annotation, node);
        for (final TypeMap map : annotation.types()) {
            if (typeName.equals(map.name()))
                return map.type();
        }
        throw new InvalidAnnotation(format("Missing type for '%s'", typeName));
    }
    private static String findAbstractTypeName(final XmlAbstractClass annotation, final XmlElement node) {
        if (!annotation.tag().isEmpty()) {
            if (node == null)
                throw new InvalidAnnotation(format("Missing tag '%s': there is no element to read it from", annotation.tag()));
            final XmlElement child = findChildForName(node, annotation.tag(), null);
            if (child == null) throw new InvalidAnnotation(format("Missing tag '%s' in element '%s'", annotation.tag(), node.name));
            final String typeName = child.getText();
            if (typeName == null)
                throw new InvalidAnnotation(format("Empty tag '%s' in element '%s' holds no type name", annotation.tag(), node.name));
            return typeName;
        }
        if (node == null)
            throw new InvalidAnnotation(format("Missing attribute '%s': there is no element to read it from", annotation.attribute()));
        final var attribute = node.attributes.get(annotation.attribute());
        if (attribute == null) throw new InvalidAnnotation(format("Missing attribute '%s' in element '%s'", annotation.attribute(), node.name));
        return attribute;
    }

    /**
     * The name to write for a field: the class of its value when the field carries the
     * annotation @XmlNameFromClass, and the field's own name otherwise. A null value has no
     * class to take a name from, so it falls back to the field's name.
     * @param field the field being written
     * @param o the field's value, which may be null
     * @return the name to write the element under
     */
    public static String toName(final Field field, final Object o) {
        // a null value has no class to take the name from, so fall back to the field name
        if (o != null && field.isAnnotationPresent(XmlNameFromClass.class))
            return toName(o.getClass());
        return toName(field);
    }
    /**
     * The tag name for a class: the value of its @XmlName annotation when it has one, and its
     * simple class name in lower case when it does not. An anonymous or local class has no simple
     * name, so the name of the nearest class that has one is used instead of an empty tag.
     * @param o the class to take the name of
     * @return the tag name
     */
    public static String toName(final Class<?> o) {
        if (o.isAnnotationPresent(XmlName.class))
            return o.getAnnotation(XmlName.class).value();
        // lower case the same wherever the program runs: the default locale maps an 'I' to a
        // dotless one in some places, and a tag name has to read the same everywhere
        final String name = o.getSimpleName().toLowerCase(Locale.ROOT);
        return name.isEmpty() && o.getSuperclass() != null ? toName(o.getSuperclass()) : name;
    }
    /**
     * The name of a field: the value of its @XmlName annotation when it has one, and the field's
     * own name when it does not.
     * @param field the field to take the name of
     * @return the element or attribute name
     */
    public static String toName(final Field field) {
        if (field.isAnnotationPresent(XmlName.class))
            return field.getAnnotation(XmlName.class).value();
        return field.getName();
    }
    /**
     * The name of a record component: the value of its @XmlName annotation when it has one, and
     * the component's own name when it does not.
     * @param field the record component to take the name of
     * @return the element or attribute name
     */
    public static String toName(final RecordComponent field) {
        if (field.isAnnotationPresent(XmlName.class))
            return field.getAnnotation(XmlName.class).value();
        return field.getName();
    }

    /**
     * The regular expression a value has to match, read out of the @XmlTextNode or @XmlAttribute
     * annotation; an empty string means no pattern was asked for. A field carrying neither
     * annotation has no pattern to read.
     * @param field the text node or attribute to take the pattern of
     * @return the pattern, possibly empty
     * @throws IllegalArgumentException if the field is neither a text node nor an attribute
     */
    public static String toPattern(final Field field) {
        if (field.isAnnotationPresent(XmlTextNode.class))
            return field.getAnnotation(XmlTextNode.class).pattern();
        if (field.isAnnotationPresent(XmlAttribute.class))
            return field.getAnnotation(XmlAttribute.class).pattern();
        throw new IllegalArgumentException("Pattern requested for field that doesn't have one " + field);
    }
    /**
     * The regular expression a value has to match, read out of the @XmlTextNode or @XmlAttribute
     * annotation; an empty string means no pattern was asked for. A component carrying neither
     * annotation has no pattern to read.
     * @param field the text node or attribute to take the pattern of
     * @return the pattern, possibly empty
     * @throws IllegalArgumentException if the component is neither a text node nor an attribute
     */
    public static String toPattern(final RecordComponent field) {
        if (field.isAnnotationPresent(XmlTextNode.class))
            return field.getAnnotation(XmlTextNode.class).pattern();
        if (field.isAnnotationPresent(XmlAttribute.class))
            return field.getAnnotation(XmlAttribute.class).pattern();
        throw new IllegalArgumentException("Pattern requested for field that doesn't have one " + field);
    }


    /**
     * The element type of a collection, taken from the first type argument of the field's generic
     * type. A raw declaration, or one whose type is a type variable, fails the cast.
     * @param f the collection field to take the element type of
     * @return the class of the elements
     */
    public static Class<?> toClassOfCollection(final Field f) {
        final ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }
    /**
     * The element type of a collection, taken from the first type argument of the component's
     * generic type. A raw declaration, or one whose type is a type variable, fails the cast.
     * @param f the collection component to take the element type of
     * @return the class of the elements
     */
    public static Class<?> toClassOfCollection(final RecordComponent f) {
        final ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
        return (Class<?>) stringListType.getActualTypeArguments()[0];
    }
    /**
     * The key type of a map, taken from the first of its two type arguments.
     * @param type the generic type of the map
     * @return the class of the keys
     */
    public static Class<?> toClassOfMapKey(final ParameterizedType type) {
        return (Class<?>)type.getActualTypeArguments()[0];
    }
    /**
     * The value type of a map, taken from the second of its two type arguments.
     * @param type the generic type of the map
     * @return the class of the values
     */
    public static Class<?> toClassOfMapValue(final ParameterizedType type) {
        return (Class<?>)type.getActualTypeArguments()[1];
    }

    /**
     * Every primitive type paired with the class that boxes it, {@code void} included.
     */
    public static final Map<Class<?>, Class<?>> PRIMITIVE_TO_OBJECT = primitiveToObject();
    private static Map<Class<?>, Class<?>> primitiveToObject() {
        final Map<Class<?>, Class<?>> classes = new HashMap<>();
        classes.put(boolean.class, Boolean.class);
        classes.put(byte.class, Byte.class);
        classes.put(char.class, Character.class);
        classes.put(double.class, Double.class);
        classes.put(float.class, Float.class);
        classes.put(int.class, Integer.class);
        classes.put(long.class, Long.class);
        classes.put(short.class, Short.class);
        classes.put(void.class, Void.class);
        return classes;
    }
    /**
     * Boxes a primitive class so the rest of the code can treat it as one ordinary reference
     * type; any class that isn't primitive is handed back unchanged.
     * @param <T> the type the class stands for
     * @param clazz the class to box
     * @return the boxed class, or the given class when it wasn't primitive
     */
    public static <T> Class<T> toObjectClass(final Class<T> clazz) {
        return clazz.isPrimitive() ? (Class<T>) PRIMITIVE_TO_OBJECT.get(clazz) : clazz;
    }

    /**
     * Reads a field by handing the element to the function named in its @XmlFieldDeserializer
     * annotation, which leaves the shape of the value to user code. The function is static and
     * takes the element as its only argument.
     * @param f the annotated field
     * @param element the element holding the value
     * @return whatever the deserializer function returns
     * @throws InvalidAnnotation if the class or the function cannot be found or invoked
     */
    public static Object invokeFieldDeserializer(final Field f, final XmlElement element) {
        final XmlFieldDeserializer annotation = f.getAnnotation(XmlFieldDeserializer.class);
        try {
            return Class.forName(annotation.clazz()).getMethod(annotation.function(), XmlElement.class).invoke(null, element);
        } catch (final NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            throw new InvalidAnnotation("FieldDeserializer " + annotation.clazz() + "." + annotation.function() + " could not be invoked", e);
        }
    }
    /**
     * Reads a record component by handing the element to the function named in its
     * {@code @XmlFieldDeserializer} annotation, which leaves the shape of the value to user
     * code. The function is static and takes the element as its only argument.
     * @param f the annotated record component
     * @param element the element holding the value
     * @return whatever the deserializer function returns
     * @throws InvalidAnnotation if the class or the function cannot be found or invoked
     */
    public static Object invokeFieldDeserializer(final RecordComponent f, final XmlElement element) {
        final XmlFieldDeserializer annotation = f.getAnnotation(XmlFieldDeserializer.class);
        try {
            return Class.forName(annotation.clazz()).getMethod(annotation.function(), XmlElement.class).invoke(null, element);
        } catch (final NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            throw new InvalidAnnotation("FieldDeserializer " + annotation.clazz() + "." + annotation.function() + " could not be invoked", e);
        }
    }

    /**
     * A primitive with no value in the XML falls back to its zero value, which is what a primitive
     * field of a class that is absent from the document would hold anyway. Anything that isn't
     * primitive has no zero value, so null is returned for those.
     * @param type the declared type of the field or record component
     * @return the zero value of the type, or null
     */
    public static Object zeroValueOf(final Class<?> type) {
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0f;
        if (type == double.class) return 0d;
        if (type == int.class) return 0;
        return null;
    }

    /**
     * Assigns a value to a field, turning a reflective failure into an AssignmentFailure rather
     * than letting IllegalAccessException escape to the caller.
     * @param field the field to assign
     * @param object the object to assign it on
     * @param value the value to assign
     */
    public static void setField(final Field field, final Object object, final Object value) {
        try {
            field.set(object, value);
        } catch (final IllegalAccessException e) {
            throw new AssignmentFailure(e);
        }
    }

}
