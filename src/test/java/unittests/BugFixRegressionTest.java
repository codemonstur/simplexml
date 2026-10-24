package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.XmlParser;
import xmlparser.annotations.*;
import xmlparser.error.InvalidAnnotation;
import xmlparser.error.InvalidXml;
import xmlparser.model.XmlElement;
import xmlparser.utils.Builder;
import xmlparser.utils.Escaping;
import xmlparser.utils.IO;
import xmlparser.utils.Reflection;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static xmlparser.utils.Escaping.unescapeHtml;
import static xmlparser.utils.Escaping.unescapeXml;

/**
 * The fixes for the bugs found in a review of the library, one method per group. Each one holds
 * the smallest document that used to be read wrong, written wrong, or not read at all, so that
 * the behaviour the review changed is written down where the next change has to keep it.
 */
public class BugFixRegressionTest {

    private final XmlParser parser = new XmlParser();

    @Test
    public void namedEntityTableDecodesToTheRightCharacters() {
        assertEquals("\u00A0", unescapeHtml("&nbsp;"), "&nbsp; is U+00A0");
        assertEquals("\u00A0", unescapeHtml("&NonBreakingSpace;"), "&NonBreakingSpace; is U+00A0");
        assertEquals("\u20DC", unescapeHtml("&DotDot;"), "&DotDot; is U+20DC");
        assertEquals("\u0311", unescapeHtml("&DownBreve;"), "&DownBreve; is U+0311");
        assertEquals("\u20DB", unescapeHtml("&tdot;"), "&tdot; is U+20DB");
        assertEquals("\u20DB", unescapeHtml("&TripleDot;"), "&TripleDot; is U+20DB");
    }

    static class NamedForSyntheticCheck { }

    @Test
    public void anAnonymousClassTakesTheNameOfItsSuperclass() {
        final Object anonymous = new NamedForSyntheticCheck() { };
        assertEquals("namedforsyntheticcheck", Reflection.toName(anonymous.getClass()),
                "An anonymous class has no name of its own, so its superclass names it");
    }

    static class WithInner {
        WithInner() { inner = new Inner(); }
        Inner inner;
        class Inner { String x = "hi"; }
    }

    @Test
    public void theSyntheticOuterReferenceIsNeverWritten() {
        final String xml = parser.toXml(new WithInner());
        assertFalse(xml.contains("this$0"), "The compiler's synthetic this$0 reference was written as content: " + xml);
        final WithInner back = parser.fromXml(xml, WithInner.class);
        assertEquals("hi", back.inner.x);
    }

    static class ListHolder { List<String> items = new ArrayList<>(); }

    @Test
    public void aTextNodeAmongCollectionItemsIsNotAFailure() {
        // the text run before the first item has no name at all, and the loop over the children
        // used to follow that name straight into an exception
        final ListHolder actual = parser.fromXml(
                "<listholder>loose text<items>a</items><items>b</items></listholder>", ListHolder.class);
        assertEquals(2, actual.items.size(), "The named children are still read around the text node");
        assertEquals("a", actual.items.get(0));
        assertEquals("b", actual.items.get(1));
    }

    static class MissingText { @XmlTextNode String text; }

    @Test
    public void aTextOrNodeFieldTheDocumentDoesNotHaveGivesNull() {
        assertNull(parser.fromXml("<missingtext/>", MissingText.class).text,
                "A @XmlTextNode field with no element to read must give null");
    }

    @Test
    public void contentAfterTheRootIsRejectedCleanly() {
        try {
            parser.fromXml("<a/>junk<b/>");
            fail("Text after the root was accepted");
        } catch (final InvalidXml e) {
            assertTrue(e.getMessage().startsWith("Content after the end of the root element"),
                    "Wrong message: " + e.getMessage());
        }

        try {
            parser.fromXml("<a/>trailing");
            fail("Leftover text at the end of the input was accepted");
        } catch (final InvalidXml e) {
            assertTrue(e.getMessage().startsWith("Found remaining text after last tag"),
                    "Wrong message: " + e.getMessage());
        }
    }

    @Test
    public void blanknessDecidesWhatAfterTheRootCounts() {
        final XmlElement blank = parser.fromXml("<a/><![CDATA[ ]]>");
        assertEquals("a", blank.name,
                "Whitespace in a section after the root is not content");

        try {
            parser.fromXml("<a/><![CDATA[now x]]>");
            fail("Non-blank characters in a section after the root were accepted");
        } catch (final InvalidXml e) {
            assertTrue(e.getMessage().startsWith("Content after the end of the root element"),
                    "Wrong message: " + e.getMessage());
        }
    }

    @Test
    public void inputThatStopsInsideATagReportsInvalidXml() {
        try {
            parser.fromXml("<a");
            fail("An unfinished tag was accepted");
        } catch (final InvalidXml e) {
            assertEquals("Unclosed tag", e.getMessage());
        }
    }

    static class Empty { String nothing = null; }

    @Test
    public void nullTextIsNeverWrittenAsTheWordNull() {
        final String xml = parser.toXml(new Empty());
        assertFalse(xml.contains("null"), "A null field was written as the literal word null: " + xml);

        final XmlElement read = parser.fromXml("<a></a>");
        assertFalse(parser.toXml(read).contains("null"), "An element with no text written back added something: " + parser.toXml(read));
    }

    static class ListWithNull { List<String> items = new ArrayList<>(Arrays.asList("a", null, "b")); }
    static class SetWithNull { Set<String> items = new LinkedHashSet<>(Arrays.asList("a", null)); }

    @Test
    public void aNullItemStaysNullThroughWritingAndReading() {
        final ListWithNull list = parser.fromXml(parser.toXml(new ListWithNull()), ListWithNull.class);
        assertEquals(3, list.items.size(), "The list kept its shape around the null");
        assertNull(list.items.get(1), "The null item came back as something else");

        final SetWithNull set = parser.fromXml(parser.toXml(new SetWithNull()), SetWithNull.class);
        assertTrue(set.items.contains(null), "A null survived a set round trip");
    }

    enum Suit { HEART, SPADE }
    static class Cards { List<Suit> suits = new ArrayList<>(List.of(Suit.HEART)); Suit one = Suit.SPADE; }

    @Test
    public void enumsInsideListsAndFieldsReadBackAsConstants() {
        final Cards back = parser.fromXml(parser.toXml(new Cards()), Cards.class);
        assertEquals(List.of(Suit.HEART), back.suits);
        assertEquals(Suit.SPADE, back.one);
    }

    static class ListWithNullEnum { List<Suit> suits = new ArrayList<>(Arrays.asList(Suit.HEART, null)); }

    @Test
    public void aNullEnumInsideAListReadsAsNullRatherThanCrashing() {
        final ListWithNullEnum back = parser.fromXml(parser.toXml(new ListWithNullEnum()), ListWithNullEnum.class);
        assertEquals(2, back.suits.size());
        assertNull(back.suits.get(1));
    }

    enum WithBody {
        A { @Override public String tag() { return "a"; } },
        B { @Override public String tag() { return "b"; } };
        public abstract String tag();
    }
    static class BodyHolder { WithBody e = WithBody.B; }

    @Test
    public void aConstantWithABodyOfItsOwnIsStillItsEnum() {
        final BodyHolder back = parser.fromXml(parser.toXml(new BodyHolder()), BodyHolder.class);
        assertEquals(WithBody.B, back.e, "A constant with a body is an anonymous class, not a new type");
    }

    static class ObjectField { Object value = Suit.SPADE; }

    @Test
    public void anObjectFieldIsWrittenFromWhatItHolds() {
        final String xml = parser.toXml(new ObjectField());
        assertTrue(xml.contains("SPADE"), "The runtime type was not used to write the value: " + xml);
    }

    static class Base { String id = "base"; }
    static class Sub extends Base { String id = "sub"; }

    @Test
    public void aShadowedFieldWritesOnceAndKeepsTheSubclassValue() throws Exception {
        final Sub got = parser.fromXml(parser.toXml(new Sub()), Sub.class);

        assertEquals("sub", got.id, "The subclass's own value is the one written and read");
        final Field hidden = Base.class.getDeclaredField("id");
        hidden.setAccessible(true);
        assertEquals("sub", got.id, "One <id> element means one value, and the hidden field must not be given "
                + "the subclass's value");
        assertNull(hidden.get(got), "The superclass's hidden field is left alone rather than filled with a value "
                + "that belongs to another field");
    }

    static class QueueHolder { Queue<String> queue = new ArrayDeque<>(List.of("a", "b")); }

    @Test
    public void aQueueIsACollectionAndReadsLikeAnyOther() {
        final QueueHolder back = parser.fromXml(parser.toXml(new QueueHolder()), QueueHolder.class);
        assertEquals(List.of("a", "b"), new ArrayList<>(back.queue), "A Queue was not treated as a collection");
    }

    static class Payload { String v = "p"; }
    static class Message {
        @XmlAbstractClass(attribute = "type",
                types = {@XmlAbstractClass.TypeMap(name = "a", type = Payload.class)})
        Object payload;
    }

    @Test
    public void anAbstractFieldWithoutItsMarkerIsRejected() {
        try {
            parser.fromXml("<message/>", Message.class);
            fail("A field typed only by an attribute the document does not have was accepted");
        } catch (final InvalidAnnotation e) {
            assertTrue(e.getMessage().contains("'type'"), "Wrong message: " + e.getMessage());
        }
    }

    // ---------- #10 and #21: what sits between elements, and the iterator's contract ----------

    static class StreamPojo { String id; }

    @Test
    public void streamingSkipsWhatSitsBetweenElementsWithoutStopping() throws Exception {
        final var doms = parser.iterateDom("<a>x</a><!-- tail -->");
        int elements = 0;
        while (doms.hasNext()) {
            assertNotNull(doms.next(), "The iterator handed back a gap");
            elements++;
        }
        assertEquals(1, elements, "A comment after the last element stopped the iteration");

        final var objects = parser.iterateObject(
                new ByteArrayInputStream("<a><id>1</id></a><?tail?>".getBytes(UTF_8)), StreamPojo.class);
        int objectsRead = 0;
        while (objects.hasNext()) {
            assertNotNull(objects.next());
            objectsRead++;
        }
        assertEquals(1, objectsRead, "A prolog after the last element stopped the iteration");
    }

    @Test
    public void hasNextCanBeAskedTwiceAndNextCanGoFirst() throws Exception {
        final var raw = parser.iterateXml("<a>x</a><b>y</b>");
        assertTrue(raw.hasNext(), "The first hasNext found nothing");
        assertTrue(raw.hasNext(), "Asking again lost the item that was already looked at");
        assertEquals("<a>x</a>", raw.next(), "The item read ahead was not the one handed back");

        final var fresh = parser.iterateXml("<a>x</a><b>y</b>");
        assertEquals("<a>x</a>", fresh.next(), "next() without hasNext() read past the item");
        assertEquals("<b>y</b>", fresh.next(), "and the iterator carries on from there");
        assertFalse(fresh.hasNext(), "The iterator did not run out");
    }

    @Test
    public void aDeclarationBeforeTheRootIsSkipped() {
        final XmlElement doc = parser.fromXml("<!DOCTYPE html><html><a>x</a></html>");
        assertEquals("x", doc.findChildForName("a", null).getText());
    }

    @Test
    public void aDeclarationWithAnInternalSubsetIsSkippedWhole() {
        final XmlElement doc = parser.fromXml(
                "<!DOCTYPE note [ <!ELEMENT note ANY> ]><note>x</note>");
        assertEquals("x", doc.getText());
    }

    @Test
    public void aPrologWhoseDataHoldsAnUnquotedGreaterThanDoesNotSpillIntoTheDocument() {
        final XmlElement doc = parser.fromXml("<?php if (a > b) { ?><a>x</a>");
        assertEquals("x", doc.getText());
    }

    @Test
    public void aPrologEndingAtABareGreaterThanLeavesTheDocumentAlone() {
        final XmlElement doc = parser.fromXml("<?xml version=\"1.0\"><a>x</a>");
        assertEquals("x", doc.getText());
    }

    @Test
    public void aCommentWhoseBodyStartsWithAGreaterThanIsStillAComment() {
        final XmlElement doc = parser.fromXml("<a><!-- > note --></a>");
        final String text = doc.getText();
        assertTrue(text == null || !text.contains("note"), "The comment leaked into the text: " + text);
    }

    @Test
    public void streamingReadsPastADeclarationBetweenElements() throws Exception {
        final var it = parser.iterateXml("<!DOCTYPE html><a>1</a><b>2</b>");
        int elements = 0;
        while (it.hasNext()) {
            it.next();
            elements++;
        }
        assertEquals(2, elements, "A declaration became an element of its own");
    }

    static class CharacterField { @XmlAttribute char c = 'x'; @XmlAttribute Character boxed = 'y'; }

    @Test
    public void anEmptyAttributeIntoACharacterIsNoCharacterRatherThanAnException() {
        final CharacterField actual = parser.fromXml("<characterfield c=\"\" boxed=\"\"/>", CharacterField.class);
        assertEquals(0, (int) actual.c, "The primitive reads the zero value the way a missing value does");
        assertNull(actual.boxed, "The boxed form reads null the way a missing value does");

        final CharacterField filled = parser.fromXml("<characterfield c=\"z\" boxed=\"q\"/>", CharacterField.class);
        assertEquals('z', filled.c);
        assertEquals(Character.valueOf('q'), filled.boxed);
    }

    static class DefaultedAttribute { @XmlAttribute @XmlDefaultValue("5") int n; String other = "x"; }
    static class DefaultedText { @XmlTextNode @XmlDefaultValue("hi") String text; }

    @Test
    public void aDefaultValueAppliesToAttributesAndTextNodesToo() {
        assertEquals(5, parser.fromXml("<defaultedattribute other=\"y\"/>", DefaultedAttribute.class).n,
                "A default was not read for a missing attribute");
        assertEquals("hi", parser.fromXml("<defaultedtext/>", DefaultedText.class).text,
                "A default was not read for a missing text node");
    }

    enum Color { RED, GREEN }
    static class Light { @XmlAttribute Color color = Color.RED; }

    @Test
    public void anEnumAttributeRoundTrips() {
        final Light back = parser.fromXml(parser.toXml(new Light()), Light.class);
        assertEquals(Color.RED, back.color);
    }

    static class PathAnchor { @XmlPath("inner") String x; String inner; }

    @Test
    public void xmlPathIsRelativeToTheDocumentItsIn() {
        final PathAnchor got = parser.fromXml("<USERS><inner><x>value</x></inner></USERS>", PathAnchor.class);
        assertEquals("value", got.x, "The path was evaluated from a name built out of the class rather than from "
                + "the root the document has");
    }

    static class NamedBody { @XmlName("custom") String id = "1"; }
    static class ConcreteEnvelope { @XmlNameFromClass NamedBody body = new NamedBody(); }
    static class TextEnvelope { @XmlNameFromClass Object body = "hi"; }

    @Test
    public void aClassNameNamesTheElementAndFindsItAgain() {
        final ConcreteEnvelope back = parser.fromXml(parser.toXml(new ConcreteEnvelope()), ConcreteEnvelope.class);
        assertNotNull(back.body, "The element named after the class was not read back");
        assertEquals("1", back.body.id);
    }

    @Test
    public void anObjectFieldWithAClassNameReadsTheTextItHolds() {
        final TextEnvelope back = parser.fromXml(parser.toXml(new TextEnvelope()), TextEnvelope.class);
        assertEquals("hi", back.body);
    }

    enum Status { ACTIVE, IDLE }
    static class StatusHolder { Status status = Status.ACTIVE; String name = "mark"; }

    @Test
    public void aCustomEnumDeserializerIsHandedTheElementsOwnText() {
        final XmlParser custom = XmlParser.newXmlParser()
                .addDeserializer(Status.class, new xmlparser.parsing.ObjectDeserializer() {
                    @Override
                    public Object convert(final String value) { return Status.valueOf(value); }

                    @Override
                    public Object convert(final XmlElement element) {
                        final String text = element.getText();
                        if (text == null || !List.of(Status.values()).contains(Status.valueOf(text)))
                            throw new AssertionError("The deserializer was handed the wrong element: "
                                    + element.name + " holding " + text);
                        return Status.valueOf(text);
                    }
                }).build();

        final StatusHolder back = custom.fromXml(custom.toXml(new StatusHolder()), StatusHolder.class);
        assertEquals(Status.ACTIVE, back.status);
        assertEquals("mark", back.name);
    }

    @Test
    public void compressionKeepsAQuoteInsideASingleQuotedValue() {
        final String compressed = parser.compressXml("<a b='say \"hi\"'><c/></a>");
        assertFalse(compressed.contains("b=\"say \""), "The double quotes were written straight back into a double quoted value: "
                + compressed);
        assertEquals("say \"hi\"", parser.fromXml(compressed).attributes.get("b"),
                "The requoted value reads back as the one that was written");
    }

    @Test
    public void compressionKeepsADeclarationAndReadsAGreaterThanOutOfAComment() {
        final String doctype = parser.compressXml("<!DOCTYPE note SYSTEM \"Note.dtd\">\n<note/>");
        assertTrue(doctype.startsWith("<!DOCTYPE note SYSTEM \"Note.dtd\">"), "The declaration was mangled: " + doctype);

        assertEquals("<a>x</a>", parser.compressXml("<!-- > note --><a>x</a>"), "A comment whose body starts with '>' was not read past");
    }

    @Test
    public void theSameAttributeUsedTwiceIsRejected() {
        try {
            parser.fromXml("<a x=\"1\" x=\"2\"/>");
            fail("The document used one name for two attributes and was accepted");
        } catch (final InvalidXml e) {
            assertEquals("Duplicate attribute: 'x'", e.getMessage());
        }
        assertEquals("1", parser.fromXml("<a x=\"1\"/>").attributes.get("x"),
                "A single attribute of that name still reads");
    }

    static class Item { String x; }

    @Test
    public void tagNamesAreLowerCasedTheSameInEveryLocale() {
        final Locale saved = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("item", Reflection.toName(Item.class),
                    "The default locale turned the 'I' of Item into its dotless form");
        } finally {
            Locale.setDefault(saved);
        }
    }

    @Test
    public void textIsOnlyANumberWhenItIsPlainlyOne() {
        assertNull(Builder.toNumber("5f"), "A type suffix is not written in a document");
        assertNull(Builder.toNumber("123L"), "A type suffix is not written in a document");
        assertNull(Builder.toNumber("0x10"), "A hexadecimal literal is not written in a document");
        assertNull(Builder.toNumber("12abc"), "Trailing junk is not a number");

        assertEquals(1.5, Builder.toNumber("1.5").doubleValue(), 0.0, "A decimal is read");
        assertEquals(-2L, Builder.toNumber("-2").longValue(), "A negative integral value is read whole");
        assertEquals(100000.0, Builder.toNumber("1e5").doubleValue(), 0.0, "An exponent is part of a plain number");
        assertEquals(9223372036854775807L, Builder.toNumber("9223372036854775807").longValue(), "A large integral value is kept whole");
    }

    @Test
    public void referencesDocumentsCannotHoldAreKeptAsText() {
        assertEquals("&#0;", unescapeXml("&#0;"), "A NUL was decoded into the document");
        assertEquals("&#1;", unescapeXml("&#1;"), "A control character was decoded into the document");
        assertEquals("&#+65;", unescapeXml("&#+65;"), "A sign is not part of a reference");
        assertEquals("&#X41;", unescapeXml("&#X41;"), "An uppercase X is not a hexadecimal marker");

        assertEquals("\t\n", unescapeXml("&#9;&#10;"), "The characters that are allowed still decode");
        assertEquals("\ud83d\ude00", unescapeXml("&#128512;"), "A supplementary character still decodes");
    }

    @Test
    public void anUnknownReferenceKeepsItsLeadingAmpersand() {
        assertEquals("AT&T", unescapeXml("AT&T"), "The ampersand was dropped: " + unescapeXml("AT&T"));
        assertEquals("&copy;", unescapeXml("&copy;"), "An unknown name was taken apart");
        assertEquals("a &", unescapeXml("a &"), "A lone ampersand was dropped");
        assertEquals("<>&\"'", unescapeXml("&lt;&gt;&amp;&quot;&apos;"), "A known reference is still read");
    }

    @Test
    public void aStringDocumentIsReadAsCharactersWhateverTheCharsetSays() throws Exception {
        try (var reader = IO.newStreamReader("<a>\u00e9</a>", US_ASCII)) {
            final char[] buffer = new char[64];
            final int read = reader.read(buffer);
            assertEquals("<a>\u00e9</a>", new String(buffer, 0, read),
                    "The text went through bytes the charset cannot hold and lost a character");
        }

        final XmlParser ascii = XmlParser.newXmlParser().charset(US_ASCII).build();
        assertEquals("\u65e5\u672c", ascii.fromXml("<a>\u65e5\u672c</a>").getText(),
                "A string document was decoded through a charset that cannot hold it");
    }

}
