package unittests;

import org.junit.Test;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The fixes for the bugs found in a review of the library, one method per group. Each one holds
 * the smallest document that used to be read wrong, written wrong, or not read at all, so that
 * the behaviour the review changed is written down where the next change has to keep it.
 */
public class BugFixRegressionTest {

    private final XmlParser parser = new XmlParser();

    // ---------- #1: the named entity table ----------

    @Test
    public void namedEntityTableDecodesToTheRightCharacters() {
        assertEquals("&nbsp; is U+00A0", "\u00A0", Escaping.unescapeHtml("&nbsp;"));
        assertEquals("&NonBreakingSpace; is U+00A0", "\u00A0", Escaping.unescapeHtml("&NonBreakingSpace;"));
        assertEquals("&DotDot; is U+20DC", "\u20DC", Escaping.unescapeHtml("&DotDot;"));
        assertEquals("&DownBreve; is U+0311", "\u0311", Escaping.unescapeHtml("&DownBreve;"));
        assertEquals("&tdot; is U+20DB", "\u20DB", Escaping.unescapeHtml("&tdot;"));
        assertEquals("&TripleDot; is U+20DB", "\u20DB", Escaping.unescapeHtml("&TripleDot;"));
    }

    // ---------- #8: synthetic members and classes with no name ----------

    static class NamedForSyntheticCheck { }

    @Test
    public void anAnonymousClassTakesTheNameOfItsSuperclass() {
        final Object anonymous = new NamedForSyntheticCheck() { };
        assertEquals("An anonymous class has no name of its own, so its superclass names it",
                "namedforsyntheticcheck", Reflection.toName(anonymous.getClass()));
    }

    static class WithInner {
        WithInner() { inner = new Inner(); }
        Inner inner;
        class Inner { String x = "hi"; }
    }

    @Test
    public void theSyntheticOuterReferenceIsNeverWritten() {
        final String xml = parser.toXml(new WithInner());
        assertFalse("The compiler's synthetic this$0 reference was written as content: " + xml,
                xml.contains("this$0"));
        final WithInner back = parser.fromXml(xml, WithInner.class);
        assertEquals("hi", back.inner.x);
    }

    // ---------- #3: null names and null nodes must not be followed blindly ----------

    static class ListHolder { List<String> items = new ArrayList<>(); }

    @Test
    public void aTextNodeAmongCollectionItemsIsNotAFailure() {
        // the text run before the first item has no name at all, and the loop over the children
        // used to follow that name straight into an exception
        final ListHolder got = parser.fromXml(
                "<listholder>loose text<items>a</items><items>b</items></listholder>", ListHolder.class);
        assertEquals("The named children are still read around the text node", 2, got.items.size());
        assertEquals("a", got.items.get(0));
        assertEquals("b", got.items.get(1));
    }

    static class MissingText { @XmlTextNode String text; }

    @Test
    public void aTextOrNodeFieldTheDocumentDoesNotHaveGivesNull() {
        assertNull("A @XmlTextNode field with no element to read must give null",
                parser.fromXml("<missingtext/>", MissingText.class).text);
    }

    // ---------- #4: what follows the root, and what the input never finished ----------

    @Test
    public void contentAfterTheRootIsRejectedCleanly() {
        try {
            parser.fromXml("<a/>junk<b/>");
            fail("Text after the root was accepted");
        } catch (final InvalidXml e) {
            assertTrue("Wrong message: " + e.getMessage(),
                    e.getMessage().startsWith("Content after the end of the root element"));
        }

        try {
            parser.fromXml("<a/>trailing");
            fail("Leftover text at the end of the input was accepted");
        } catch (final InvalidXml e) {
            assertTrue("Wrong message: " + e.getMessage(),
                    e.getMessage().startsWith("Found remaining text after last tag"));
        }
    }

    @Test
    public void blanknessDecidesWhatAfterTheRootCounts() {
        final XmlElement blank = parser.fromXml("<a/><![CDATA[ ]]>");
        assertEquals("Whitespace in a section after the root is not content", "a", blank.name);

        try {
            parser.fromXml("<a/><![CDATA[now x]]>");
            fail("Non-blank characters in a section after the root were accepted");
        } catch (final InvalidXml e) {
            assertTrue("Wrong message: " + e.getMessage(),
                    e.getMessage().startsWith("Content after the end of the root element"));
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

    // ---------- #2 and #6: null text, null items ----------

    static class Empty { String nothing = null; }

    @Test
    public void nullTextIsNeverWrittenAsTheWordNull() {
        final String xml = parser.toXml(new Empty());
        assertFalse("A null field was written as the literal word null: " + xml, xml.contains("null"));

        final XmlElement read = parser.fromXml("<a></a>");
        assertFalse("An element with no text written back added something: " + parser.toXml(read),
                parser.toXml(read).contains("null"));
    }

    static class ListWithNull { List<String> items = new ArrayList<>(Arrays.asList("a", null, "b")); }
    static class SetWithNull { Set<String> items = new LinkedHashSet<>(Arrays.asList("a", null)); }

    @Test
    public void aNullItemStaysNullThroughWritingAndReading() {
        final ListWithNull list = parser.fromXml(parser.toXml(new ListWithNull()), ListWithNull.class);
        assertEquals("The list kept its shape around the null", 3, list.items.size());
        assertNull("The null item came back as something else", list.items.get(1));

        final SetWithNull set = parser.fromXml(parser.toXml(new SetWithNull()), SetWithNull.class);
        assertTrue("A null survived a set round trip", set.items.contains(null));
    }

    // ---------- #6: enums are values, not objects to fill in field by field ----------

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
        assertEquals("A constant with a body is an anonymous class, not a new type",
                WithBody.B, back.e);
    }

    static class ObjectField { Object value = Suit.SPADE; }

    @Test
    public void anObjectFieldIsWrittenFromWhatItHolds() {
        final String xml = parser.toXml(new ObjectField());
        assertTrue("The runtime type was not used to write the value: " + xml, xml.contains("SPADE"));
    }

    // ---------- #5, #7, #9: fields, collections, abstract types ----------

    static class Base { String id = "base"; }
    static class Sub extends Base { String id = "sub"; }

    @Test
    public void aShadowedFieldWritesOnceAndKeepsTheSubclassValue() throws Exception {
        final Sub got = parser.fromXml(parser.toXml(new Sub()), Sub.class);

        assertEquals("The subclass's own value is the one written and read", "sub", got.id);
        final Field hidden = Base.class.getDeclaredField("id");
        hidden.setAccessible(true);
        assertEquals("One <id> element means one value, and the hidden field must not be given "
                        + "the subclass's value", "sub", got.id);
        assertNull("The superclass's hidden field is left alone rather than filled with a value "
                + "that belongs to another field", hidden.get(got));
    }

    static class QueueHolder { Queue<String> queue = new ArrayDeque<>(List.of("a", "b")); }

    @Test
    public void aQueueIsACollectionAndReadsLikeAnyOther() {
        final QueueHolder back = parser.fromXml(parser.toXml(new QueueHolder()), QueueHolder.class);
        assertEquals("A Queue was not treated as a collection", List.of("a", "b"), new ArrayList<>(back.queue));
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
            assertTrue("Wrong message: " + e.getMessage(), e.getMessage().contains("'type'"));
        }
    }

    // ---------- #10 and #21: what sits between elements, and the iterator's contract ----------

    static class StreamPojo { String id; }

    @Test
    public void streamingSkipsWhatSitsBetweenElementsWithoutStopping() throws Exception {
        final var doms = parser.iterateDom("<a>x</a><!-- tail -->");
        int elements = 0;
        while (doms.hasNext()) {
            assertNotNull("The iterator handed back a gap", doms.next());
            elements++;
        }
        assertEquals("A comment after the last element stopped the iteration", 1, elements);

        final var objects = parser.iterateObject(
                new ByteArrayInputStream("<a><id>1</id></a><?tail?>".getBytes(UTF_8)), StreamPojo.class);
        int objectsRead = 0;
        while (objects.hasNext()) {
            assertNotNull(objects.next());
            objectsRead++;
        }
        assertEquals("A prolog after the last element stopped the iteration", 1, objectsRead);
    }

    @Test
    public void hasNextCanBeAskedTwiceAndNextCanGoFirst() throws Exception {
        final var raw = parser.iterateXml("<a>x</a><b>y</b>");
        assertTrue("The first hasNext found nothing", raw.hasNext());
        assertTrue("Asking again lost the item that was already looked at", raw.hasNext());
        assertEquals("The item read ahead was not the one handed back", "<a>x</a>", raw.next());

        final var fresh = parser.iterateXml("<a>x</a><b>y</b>");
        assertEquals("next() without hasNext() read past the item", "<a>x</a>", fresh.next());
        assertEquals("and the iterator carries on from there", "<b>y</b>", fresh.next());
        assertFalse("The iterator did not run out", fresh.hasNext());
    }

    // ---------- #11, #12, #13: declarations, prologs and comments with a '>' in them ----------

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
        assertTrue("The comment leaked into the text: " + text, text == null || !text.contains("note"));
    }

    @Test
    public void streamingReadsPastADeclarationBetweenElements() throws Exception {
        final var it = parser.iterateXml("<!DOCTYPE html><a>1</a><b>2</b>");
        int elements = 0;
        while (it.hasNext()) {
            it.next();
            elements++;
        }
        assertEquals("A declaration became an element of its own", 2, elements);
    }

    // ---------- #14: a character field the document gave no character ----------

    static class CharacterField { @XmlAttribute char c = 'x'; @XmlAttribute Character boxed = 'y'; }

    @Test
    public void anEmptyAttributeIntoACharacterIsNoCharacterRatherThanAnException() {
        final CharacterField got = parser.fromXml("<characterfield c=\"\" boxed=\"\"/>", CharacterField.class);
        assertEquals("The primitive reads the zero value the way a missing value does",
                0, (int) got.c);
        assertNull("The boxed form reads null the way a missing value does", got.boxed);

        final CharacterField filled = parser.fromXml("<characterfield c=\"z\" boxed=\"q\"/>", CharacterField.class);
        assertEquals('z', filled.c);
        assertEquals(Character.valueOf('q'), filled.boxed);
    }

    // ---------- #15 to #19 and #28: annotations that lost their value ----------

    static class DefaultedAttribute { @XmlAttribute @XmlDefaultValue("5") int n; String other = "x"; }
    static class DefaultedText { @XmlTextNode @XmlDefaultValue("hi") String text; }

    @Test
    public void aDefaultValueAppliesToAttributesAndTextNodesToo() {
        assertEquals("A default was not read for a missing attribute",
                5, parser.fromXml("<defaultedattribute other=\"y\"/>", DefaultedAttribute.class).n);
        assertEquals("A default was not read for a missing text node",
                "hi", parser.fromXml("<defaultedtext/>", DefaultedText.class).text);
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
        assertEquals("The path was evaluated from a name built out of the class rather than from "
                + "the root the document has", "value", got.x);
    }

    static class NamedBody { @XmlName("custom") String id = "1"; }
    static class ConcreteEnvelope { @XmlNameFromClass NamedBody body = new NamedBody(); }
    static class TextEnvelope { @XmlNameFromClass Object body = "hi"; }

    @Test
    public void aClassNameNamesTheElementAndFindsItAgain() {
        final ConcreteEnvelope back = parser.fromXml(parser.toXml(new ConcreteEnvelope()), ConcreteEnvelope.class);
        assertNotNull("The element named after the class was not read back", back.body);
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

    // ---------- #20: compression requoting what it cannot escape ----------

    @Test
    public void compressionKeepsAQuoteInsideASingleQuotedValue() {
        final String compressed = parser.compressXml("<a b='say \"hi\"'><c/></a>");
        assertFalse("The double quotes were written straight back into a double quoted value: "
                + compressed, compressed.contains("b=\"say \""));
        assertEquals("The requoted value reads back as the one that was written",
                "say \"hi\"", parser.fromXml(compressed).attributes.get("b"));
    }

    @Test
    public void compressionKeepsADeclarationAndReadsAGreaterThanOutOfAComment() {
        final String doctype = parser.compressXml("<!DOCTYPE note SYSTEM \"Note.dtd\">\n<note/>");
        assertTrue("The declaration was mangled: " + doctype,
                doctype.startsWith("<!DOCTYPE note SYSTEM \"Note.dtd\">"));

        assertEquals("A comment whose body starts with '>' was not read past",
                "<a>x</a>", parser.compressXml("<!-- > note --><a>x</a>"));
    }

    // ---------- #22: the same attribute used twice ----------

    @Test
    public void theSameAttributeUsedTwiceIsRejected() {
        try {
            parser.fromXml("<a x=\"1\" x=\"2\"/>");
            fail("The document used one name for two attributes and was accepted");
        } catch (final InvalidXml e) {
            assertEquals("Duplicate attribute: 'x'", e.getMessage());
        }
        assertEquals("A single attribute of that name still reads", "1",
                parser.fromXml("<a x=\"1\"/>").attributes.get("x"));
    }

    // ---------- #23: names that read the same wherever the program runs ----------

    static class Item { String x; }

    @Test
    public void tagNamesAreLowerCasedTheSameInEveryLocale() {
        final Locale saved = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("The default locale turned the 'I' of Item into its dotless form",
                    "item", Reflection.toName(Item.class));
        } finally {
            Locale.setDefault(saved);
        }
    }

    // ---------- #24: only a plain number is read as one ----------

    @Test
    public void textIsOnlyANumberWhenItIsPlainlyOne() {
        assertNull("A type suffix is not written in a document", Builder.toNumber("5f"));
        assertNull("A type suffix is not written in a document", Builder.toNumber("123L"));
        assertNull("A hexadecimal literal is not written in a document", Builder.toNumber("0x10"));
        assertNull("Trailing junk is not a number", Builder.toNumber("12abc"));

        assertEquals("A decimal is read", 1.5, Builder.toNumber("1.5").doubleValue(), 0.0);
        assertEquals("A negative integral value is read whole", -2L, Builder.toNumber("-2").longValue());
        assertEquals("An exponent is part of a plain number", 100000.0, Builder.toNumber("1e5").doubleValue(), 0.0);
        assertEquals("A large integral value is kept whole", 9223372036854775807L,
                Builder.toNumber("9223372036854775807").longValue());
    }

    // ---------- #25: references to characters no document may hold ----------

    @Test
    public void referencesDocumentsCannotHoldAreKeptAsText() {
        assertEquals("A NUL was decoded into the document", "&#0;", Escaping.unescapeXml("&#0;"));
        assertEquals("A control character was decoded into the document", "&#1;", Escaping.unescapeXml("&#1;"));
        assertEquals("A sign is not part of a reference", "&#+65;", Escaping.unescapeXml("&#+65;"));
        assertEquals("An uppercase X is not a hexadecimal marker", "&#X41;", Escaping.unescapeXml("&#X41;"));

        assertEquals("The characters that are allowed still decode", "\t\n", Escaping.unescapeXml("&#9;&#10;"));
        assertEquals("A supplementary character still decodes", "\ud83d\ude00",
                Escaping.unescapeXml("&#128512;"));
    }

    // ---------- #26: a reference this does not know ----------

    @Test
    public void anUnknownReferenceKeepsItsLeadingAmpersand() {
        assertEquals("The ampersand was dropped: " + Escaping.unescapeXml("AT&T"),
                "AT&T", Escaping.unescapeXml("AT&T"));
        assertEquals("An unknown name was taken apart", "&copy;", Escaping.unescapeXml("&copy;"));
        assertEquals("A lone ampersand was dropped", "a &", Escaping.unescapeXml("a &"));
        assertEquals("A known reference is still read", "<>&\"'",
                Escaping.unescapeXml("&lt;&gt;&amp;&quot;&apos;"));
    }

    // ---------- #27: a document already in memory is characters, not bytes ----------

    @Test
    public void aStringDocumentIsReadAsCharactersWhateverTheCharsetSays() throws Exception {
        try (var reader = IO.newStreamReader("<a>\u00e9</a>", US_ASCII)) {
            final char[] buffer = new char[64];
            final int read = reader.read(buffer);
            assertEquals("The text went through bytes the charset cannot hold and lost a character",
                    "<a>\u00e9</a>", new String(buffer, 0, read));
        }

        final XmlParser ascii = XmlParser.newXmlParser().charset(US_ASCII).build();
        assertEquals("A string document was decoded through a charset that cannot hold it",
                "\u65e5\u672c", ascii.fromXml("<a>\u65e5\u672c</a>").getText());
    }

}
