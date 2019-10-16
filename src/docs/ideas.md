# A parser listener with this kind of interface

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

# Add javadoc and jpeek badge when in maven central

Example project that has done this.
https://github.com/jknack/handlebars.java/blob/master/pom.xml

# Add a XmlNamePattern annotation that can match multiple names during deserialization

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

# Add support for @DynamicName that generates a name based on something somehow

Try to generate the XML structure given above.

# Add enum support

# After adding XPath support make it possible to generate XML using it

https://stackoverflow.com/questions/51953656/java-create-xml-using-xpath

# Make a safe version of map serialization the default

Default should be : <item key="key-here">value-here</item>
Default is now    : <key>value</key>

# Add an asymmetric name annotation

An annotation that allows you to deserialize using one name and serialize using another

# Add more support for xpath

XPath support is very simplistic at the moment. 
There are other options documented here: https://github.com/code4craft/xsoup

# Add support for namespaces

- Should be turned off by default
- Should affect name parsing
- Should check if the namespace exists
- Can warn or fail on namespace missing

# Add parser options that will cause fail-fast behavior

check to see if all fields existed in the XmlElement
check to see if generate object has values for all fields
check to see if all nodes in the XmlElement hve been mapped to a field
check to see if the used namespaces exist

# Speed up Xpath parsing

the @XmlPath annotation contains a string that denotes the XPath expression to use. 
Currently the Xpath is compiled every single time it is encountered. 
This can be sped up by using interning.

# Make self-closing tag during serialization configurable

Some people want serialization to generate empty tags like so <tag></tag>, some people want a self-closing tag: <tag/>.
Make this configurable.
// https://stackoverflow.com/questions/52194046/change-empty-tag-to-self-closing-tags-through-java
// https://stackoverflow.com/questions/52270774/java-transformer-empty-element

# Add null-to-empty annotation

An annotation that tells the serializer to create an empty tag for a null field.
