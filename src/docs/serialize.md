### Serializing

Lets start with this code:

    import simplexml.SimpleXml;
    
    public class Serialize {
    
        private static class Project {
            public final String name;
    
            private Project(final String name) {
                this.name = name;
            }
        }
    
        public static void main(final String... args) {
            final Project project = new Project("test");
    
            final SimpleXml simple = new SimpleXml();
            System.out.println(simple.toXml(project));
    
        }
    
    }

When run this will output:

    <project>
      <name>test</name>
    </project>
    
By adding the `@XmlAttribute` annotation above the name field you can turn the name tag into a name attribute.
The output from the code will look like this:

    <project name="test" />

By adding the `@XmlName("other")` annotation above the name field you can change the name of the tag or attribute.
The output from the code will look like this:

    <project other="test" />

By adding the `@XmlTextNode` annotation you can turn a field into the textnode of its enclosing class.
Lets add an extra field named `text` and give it this annotation.
The code for the Project class now looks like this:

    private static class Project {
        @XmlAttribute
        @XmlName("other")
        public final String name;

        @XmlTextNode
        public final String text;

        private Project(final String name, final String text) {
            this.name = name;
            this.text = text;
        }
    }

When we run the serializer on this class we get:

    <project other="test">Just a project</project>
    
By adding the `@XmlNoExport` annotation you can tell the serializer to ignore a field.
Lets add a field named `hidden` and give it this annotation.
The added code looks like this:

    @XmlNoExport
    public final String hidden;

When run the class still outputs the same as before:

    <project other="test">Just a project</project>

The serializer will encode dangerous characters correctly.
This is the output if we instantiate Project this way `new Project("test<>&\"'", "Just a project", "invisible");`:

    <project other="test&lt;&gt;&amp;&quot;&apos;">Just a project</project>
