package example;

import xmlparser.XmlParser;

public final class RawStream {

    private static final String STREAMABLE = """
            <tag>
                <child></child>
            </tag>
            
            <something/>
            
            <else>
                <child1></child1>
                <child2></child2>
            </else>
            """;

    public static void main(final String... args) throws Exception {
        final var parser = new XmlParser();

        final var it = parser.iterateXml(STREAMABLE);
        while (it.hasNext()) {
            System.out.println("Got an item: " + it.next());
        }
    }

}
