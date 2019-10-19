[![GitHub Release](https://img.shields.io/github/release/codemonstur/simplexml.svg)](https://github.com/codemonstur/simplexml/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/simplexml/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/simplexml)
[![Build Status](https://travis-ci.org/codemonstur/simplexml.svg?branch=master)](https://travis-ci.org/codemonstur/simplexml)
[![Maintainability](https://api.codeclimate.com/v1/badges/63924c44946973cb37f8/maintainability)](https://codeclimate.com/github/codemonstur/simplexml/maintainability)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)
[![Coverage Status](https://coveralls.io/repos/github/codemonstur/simplexml/badge.svg?branch=master)](https://coveralls.io/github/codemonstur/simplexml?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/813d8482256b4ed88e2ff1018d53f06e)](https://www.codacy.com/app/codemonstur/simplexml)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/codemonstur/simplexml)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/1621/badge)](https://bestpractices.coreinfrastructure.org/projects/1621)

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
        <version>2.0.0</version>
    </dependency>

Or alternatively you can clone the repo and install locally:

    git clone https://github.com/codemonstur/simplexml.git
    cd simplexml
    mvn install

## License

The MIT license. And I left out the copyright notice everywhere because I just don't care.

## Future Ideas

There are a bunch of features that I could add.
In no particular order:
- Support comments
- Support CDATA blocks
- XML streaming
  - Push and Pull
  - object, xpath
- Validation
