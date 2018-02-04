[![Build Status](https://travis-ci.org/JurgenNED/simplexml.svg?branch=master)](https://travis-ci.org/JurgenNED/simplexml)
[![Maintainability](https://api.codeclimate.com/v1/badges/c9389fa8729d8b18e46b/maintainability)](https://codeclimate.com/github/JurgenNED/simplexml/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/c9389fa8729d8b18e46b/test_coverage)](https://codeclimate.com/github/JurgenNED/simplexml/test_coverage)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)
[![HitCount](http://hits.dwyl.com/JurgenNED/simplexml.svg)](http://hits.dwyl.com/JurgenNED/simplexml)
[![Dependency Status](https://www.versioneye.com/user/projects/5a76d5b10fb24f17ce755df9/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5a76d5b10fb24f17ce755df9)
[![Coverage Status](https://coveralls.io/repos/github/JurgenNED/simplexml/badge.svg?branch=master)](https://coveralls.io/github/JurgenNED/simplexml?branch=master)
[![PDD status](http://www.0pdd.com/svg?name=JurgenNED/simplexml)](http://www.0pdd.com/p?name=JurgenNED/simplexml)

# SimpleXML

After a number of bad experiences with XML parsing in Java I decided to write my own parser.
The goals for this project are this:

1. Correct XML parsing

The code must generate valid XML when (de)serializing and correctly encode/decode dangerous characters.
  
2. No-dependencies

The code must not depend on other code. 
And I do mean nothing, it doesn't even depend on javax.xml stuff.
This library should be easy to include in a jigsaw project, although I haven't tried yet.

3. Thread-safe

Don't do crazy stuff like implement state in a custom serializer.
Don't change the list of serializers and deserializers while you are using it.
Other than those rules the parser is safe to use with concurrency.

4. User friendly API

What's with all the builders and factories and god knows what?
This library is simple.
Create an instance of SimpleXml and call toXml() or fromXml().

## How to use

The unit tests in `src/test/java` can give you a good idea of the capabilities of SimpleXml.
There is also a small example program in `src/test/java/tools/Serialize.java` that shows of basic serializing.

### Serializing

The simplest case possible:

    public class MyObject {
        String name;
    }
    
    MyObject object = new MyObject();
    object.name = "test";

    final SimpleXml simple = new SimpleXml();
    System.out.println(simple.toXml(project));

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
    final MyObject object = simple.fromXml("<myobject><name>test</name></myobject>");
    System.out.println(object.name);

This code will output:

    test

The deserializer will respect the same annotations as the serializer
- Renamed fields
- Attributes
- Text nodes
- Skipped fields

## How to get

I haven't put it in maven central yet.
I'd like to but just haven't gotten to it.
For now just clone the repo and install locally:

    git clone https://github.com/JurgenNED/simplexml.git
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
  - raw, document, object, xpath
- Validation
- XPath querying
- Builders for immutable Reader, Writer and Parser
- Builder for ElementNode
