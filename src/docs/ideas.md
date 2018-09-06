- A parser listener with this kind of interface

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

- Add javadoc and jpeek badge when in maven central
  - https://github.com/jknack/handlebars.java/blob/master/pom.xml

- Add a XmlNamePattern annotation that can match multiple names during deserialization

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

- Add support for @DynamicName that generates a name based on something somehow

Try to generate the XML structure given above.

- Add enum support

- After adding XPath support make it possible to generate XML using it

https://stackoverflow.com/questions/51953656/java-create-xml-using-xpath

- Add annotation that configures serialization of a Map

Output could be: <item key="key-here">value-here</item>
Or it could be : <key>value</key>

The second one is what it does now but it can create invalid XML. Default should be first behaviour. Use
annotation for second.

