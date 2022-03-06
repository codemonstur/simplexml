# Should haves

## Add more support for xpath

XPath support is very simplistic at the moment. 
There are other options documented here: https://github.com/code4craft/xsoup

Current XPath support was copied, with permission, from another project.
The code is rudimentary and, in my opinion, kinda ugly.
But it works.

It really should be rewritten from scratch.
With full support for everything you can do with XPath.

## Add support for namespaces

I dislike namespaces, they add no real value and cause parsing problems.
I therefore didn't add any support for it at all.
As far as the parser is concerned, namespaces are just attributes and strange names.
But there really ought to be some sort of support for dealing with namespaces.

- Should be turned off by default
- Should affect name parsing
- Should check if the namespace exists
- Can warn or fail on namespace missing

## Add support for CDATA blocks

CDATA blocks are another weird feature in XML that really shouldn't exist at all.
But it does exist and it would be nice to have a way to make the parser correctly parse them.

https://en.wikipedia.org/wiki/CDATA


# Nice to haves

## XmlSerialization annotation

Add an annotation that marks a method as a serializer for a given object.

## XmlDeserialization annotation

Add an annotation that marks a method as a deserializer for a given object.

## Remove need for objenesis

Currently, the code has a single dependency; objenesis.
The library does its job, but I would rather have no dependencies at all.

It was added to create deserialization support for all versions of Java.
I don't remember exactly which versions of Java you would lose support for if you get rid of it.

## XmlDynamicList annotation

An annotation for a special type of list that; has a wrapper tag,
and the name of the item determines the type of the item. So:

```
<list>
    <string>String here</string>
    <double>2.5</double>
</list>
```

We want to turn that into; `List<Object> list`.

```
@Retention(RUNTIME)
public @interface XmlDynamicList {
    @interface TypeMap {
        String name();
        Class<?> type();
    }

    String name();
    TypeMap[] types();
}
```

# Maybe haves

## Add parser options that will cause fail-fast behavior

check to see if all fields existed in the XmlElement
check to see if generate object has values for all fields
check to see if all nodes in the XmlElement have been mapped to a field
check to see if the used namespaces exist

## Add an @XmlMandatory tag

If the tag exists, but the data is missing, throw a serialization exception

## Speed up Xpath parsing

The @XmlPath annotation contains a string that denotes the XPath expression to use. 
Currently, the Xpath is compiled every single time it is encountered. 
This can be sped up by using interning.

## Make self-closing tag during serialization configurable

Some people want serialization to generate empty tags like so <tag></tag>, some people want a self-closing tag: <tag/>.
Make this configurable.
// https://stackoverflow.com/questions/52194046/change-empty-tag-to-self-closing-tags-through-java
// https://stackoverflow.com/questions/52270774/java-transformer-empty-element

## Add null-to-empty annotation

An annotation that tells the serializer to create an empty tag for a null field.

## XML validation

## XML transforming

## XML compare / diff

## A parser listener with this kind of interface

public interface EventHandler {
  void handle(String value);
}

Parser parser = newParser()
    .on(START_ELEMENT, handler)
    .on(ATTRIBUTE_NAME, handler)
    .on(ATTRIBUTE_VALUE, handler)
    .on(VALUE, handler)
    .on(END_ELEMENT, handler)
    .on(ERROR, handler)
    .build();
    
Don't care that much about this anymore

## Add a XmlNamePattern annotation that can match multiple names during deserialization

https://stackoverflow.com/questions/51964053/convert-liststring-into-below-xml-format-in-java
https://stackoverflow.com/questions/19834756/jaxb-unmarshalling-with-dynamic-elements

<xx>
    <s1>
        <X>-9999</X>
        <Y>-9999</Y>
    </s1>
    <s2>
        <X>-9999</X>
        <Y>-9999</Y>
   </s2>
</xx>

Use interning to speed up the use of the regular expressions

## Add support for @DynamicName that generates a name based on something somehow

Try to generate the XML structure given above.
Can probably be done using generex.
But I don't want to add another dependency.

## After adding XPath support make it possible to generate XML using it

https://stackoverflow.com/questions/51953656/java-create-xml-using-xpath

## Make a safe version of map serialization the default

Default should be : <item key="key-here">value-here</item>
Default is now    : <key>value</key>

The multiple ways of serializing maps already exists.
For now, I've kept the original way for backwards compatibility reasons.

## Upgrade to Java 11

The code targets Java 8.
This is primarily because XML parsing is common in legacy projects that are still on older versions of Java.
It also means the code has some uglinesses that don't have to be there on Java 11.

Java 11 has private interface methods for example. It also has the var keyword.
Java 13 adds an enhanced switch that can make a central part of the code prettier.
I'd like to use it but Java 13 is very new.

At some point Java 8 will be so old supporting it is not necessary anymore.
