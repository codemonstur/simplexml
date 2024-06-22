
[![GitHub Release](https://img.shields.io/github/release/codemonstur/simplexml.svg)](https://github.com/codemonstur/simplexml/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/simplexml/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/simplexml)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

# SimpleXml

After a number of bad experiences with XML parsing in Java I decided to write my own parser.

This project has the following characteristics; correct XML parsing, small, few dependencies, 
thread-safe, user friendly API, secure.

Version 3.x depends on Java 19. 
For Java 8 support you have to use 2.x versions.

## How to use

The unit tests in [src/test/java/unittests](https://github.com/codemonstur/simplexml/tree/master/src/test/java/unittests) can give you a good idea of the capabilities of SimpleXml.
There are also a number of small example programs in [src/test/java/example](https://github.com/codemonstur/simplexml/tree/master/src/test/java/example) that showcase various features.

### Examples

* [Builder.java](https://github.com/codemonstur/simplexml/blob/master/src/test/java/example/Builder.java)
* [Compress.java](https://github.com/codemonstur/simplexml/blob/master/src/test/java/example/Compress.java)
* [Deserialize.java](https://github.com/codemonstur/simplexml/blob/master/src/test/java/example/Deserialize.java)
* [PrettyPrint.java](https://github.com/codemonstur/simplexml/blob/master/src/test/java/example/PrettyPrint.java)
* [RawStream.java](https://github.com/codemonstur/simplexml/blob/master/src/test/java/example/RawStream.java)
* [Serialize.java](https://github.com/codemonstur/simplexml/blob/master/src/test/java/example/Serialize.java)

## Implemented annotations

- [@XmlAbstractClass](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlAbstractClass.java)
- [@XmlAttribute](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlAttribute.java)
- [@XmlEnumValue](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlEnumValue.java)
- [@XmlFieldDeserializer](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlFieldDeserializer.java)
- [@XmlMapTagIsKey](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlMapTagIsKey.java)
- [@XmlMapWithAttributes](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlMapWithAttributes.java)
- [@XmlMapWithChildNodes](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlMapWithChildNodes.java)
- [@XmlName](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlName.java)
- [@XmlNameFromClass](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlNameFromClass.java)
- [@XmlNoExport](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlNoExport.java)
- [@XmlNoImport](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlNoImport.java)
- [@XmlObjectValidator](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlObjectValidator.java)
- [@XmlPath](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlPath.java)
- [@XmlTextNode](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlTextNode.java)
- [@XmlWrapperTag](https://github.com/codemonstur/simplexml/blob/master/src/main/java/xmlparser/annotations/XmlWrapperTag.java)

## How to get

It's in maven central.

    <dependency>
        <groupId>com.github.codemonstur</groupId>
        <artifactId>simplexml</artifactId>
        <version>3.2.0</version>
    </dependency>

### Serializing

The simplest case possible:

    import xmlparser.XmlParser;

    public class MyObject {
        String name;
    }
    
    MyObject object = new MyObject();
    object.name = "test";

    final XmlParser parser = new XmlParser();
    System.out.println(parser.toXml(object));

This code will output:

    <myobject>
      <name>test</name>
    </myobject>

There are more serialization options
- Renaming fields
- Fields as attributes
- Field as text node
- Skipping fields
- etc...

For more documentation on serializing look in [src/docs/serialize.md](https://github.com/codemonstur/simplexml/tree/master/src/docs/serialize.md).

### Deserializing

The simplest case possible:

    import xmlparser.XmlParser;

    public class MyObject {
        String name;
    }

    final XmlParser parser = new XmlParser();
    final MyObject object = parser.fromXml("<myobject><name>test</name></myobject>", MyObject.class);
    System.out.println(object.name);

This code will output:

    test

The deserializer will respect the same annotations as the serializer
- Renamed fields
- Attributes
- Text nodes
- Skipped fields
- etc...

If there is a feature you would like to have, but doesn't exist, please let me know.

## License

The MIT license. And I left out the copyright notice everywhere because I just don't care.

