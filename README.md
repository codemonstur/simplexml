
[![GitHub Release](https://img.shields.io/github/release/codemonstur/simplexml.svg)](https://github.com/codemonstur/simplexml/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/simplexml/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/simplexml)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

# SimpleXml

After a number of bad experiences with XML parsing in Java I decided to write my own parser.

This project has the following characteristics; correct XML parsing, small, few dependencies, 
thread-safe, user friendly API, secure.

Version 3.0.0 depends on Java 16. 
For Java 8 support you have to use 2.9.0 or earlier.

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

## How to get

It's in maven central.

    <dependency>
        <groupId>com.github.codemonstur</groupId>
        <artifactId>simplexml</artifactId>
        <version>3.1.0</version>
    </dependency>

### Serializing

The simplest case possible:

    public class MyObject {
        String name;
    }
    
    MyObject object = new MyObject();
    object.name = "test";

    final SimpleXml simple = new SimpleXml();
    System.out.println(simple.toXml(object));

This code will output:

    <myobject>
      <name>test</name>
    </myobject>

There are more serialization options
- Renaming fields
- Fields as attributes
- Field as text node
- Skipping fields

For more documentation on serializing look in [src/docs/serialize.md](https://github.com/codemonstur/simplexml/tree/master/src/docs/serialize.md).

### Deserializing

The simplest case possible:

    public class MyObject {
        String name;
    }

    final SimpleXml simple = new SimpleXml();
    final MyObject object = simple.fromXml("<myobject><name>test</name></myobject>", MyObject.class);
    System.out.println(object.name);

This code will output:

    test

The deserializer will respect the same annotations as the serializer
- Renamed fields
- Attributes
- Text nodes
- Skipped fields

## License

The MIT license. And I left out the copyright notice everywhere because I just don't care.

