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

- Add maven central, add javadoc and jpeek badge when in maven central
  - http://datumedge.blogspot.nl/2012/05/publishing-from-github-to-maven-central.html
  - http://central.sonatype.org/pages/ossrh-guide.html
  - http://central.sonatype.org/pages/apache-maven.html
  - http://central.sonatype.org/pages/requirements.html
  - https://github.com/jknack/handlebars.java/blob/master/pom.xml