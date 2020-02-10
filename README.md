
[![Build Status](https://travis-ci.org/codemonstur/simplexml.svg?branch=master)](https://travis-ci.org/codemonstur/simplexml)
[![GitHub Release](https://img.shields.io/github/release/codemonstur/simplexml.svg)](https://github.com/codemonstur/simplexml/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/simplexml/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/simplexml)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

# SimpleXML

After a number of bad experiences with XML parsing in Java I decided to write my own parser.

This project has the following characteristics; correct XML parsing, small, few dependencies, 
thread-safe, user friendly API, secure.

## How to use

The unit tests in `src/test/java/unittests` can give you a good idea of the capabilities of SimpleXml.
There are also a number of small example programs in `src/test/java/tools` that showcase various features.

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

For more documentation on serializing look in `src/docs/serialize.md`.

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

## How to get

Its in maven central.

    <dependency>
        <groupId>com.github.codemonstur</groupId>
        <artifactId>simplexml</artifactId>
        <version>2.6.0</version>
    </dependency>

Or alternatively you can clone the repo and install locally:

    git clone https://github.com/codemonstur/simplexml.git
    cd simplexml
    mvn install

## License

The MIT license. And I left out the copyright notice everywhere because I just don't care.

