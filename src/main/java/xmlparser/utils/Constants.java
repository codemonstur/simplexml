package xmlparser.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The characters, tag fragments, error messages and limits the rest of the code refers to by
 * name, so that a stray typo in a symbol shows up as a missing constant instead of as a document
 * that quietly misparses. Also holds the named entities the library knows by heart, read from a
 * resource in the jar the first time they are needed.
 */
public enum Constants {;

    /**
     * The {@code '/'} character.
     */
    public static final char CHAR_FORWARD_SLASH = '/';
    /**
     * The space character.
     */
    public static final char CHAR_SPACE = ' ';
    /**
     * The {@code '='} character.
     */
    public static final char CHAR_EQUALS = '=';
    /**
     * The {@code '<'} character.
     */
    public static final char CHAR_LESS_THAN = '<';
    /**
     * The {@code '>'} character.
     */
    public static final char CHAR_GREATER_THAN = '>';
    /**
     * The {@code '?'} character.
     */
    public static final char CHAR_QUESTION_MARK = '?';
    /**
     * The {@code '!'} character.
     */
    public static final char CHAR_EXCLAMATION_MARK = '!';
    /**
     * The {@code '\''} character.
     */
    public static final char CHAR_SINGLE_QUOTE = '\'';
    /**
     * The {@code '"'} character.
     */
    public static final char CHAR_DOUBLE_QUOTE = '"';
    /**
     * The {@code '&'} character.
     */
    public static final char CHAR_AMPERSAND = '&';
    /**
     * The replacement character, U+FFFD.
     */
    // Stands in for an unpaired surrogate, which is not a character and cannot be written
    // into an XML document
    public static final char CHAR_REPLACEMENT = '\uFFFD';

    /**
     * The character that opens a tag, {@code '<'}.
     */
    public static final char XML_TAG_START = CHAR_LESS_THAN;
    /**
     * The character that closes a tag, {@code '>'}.
     */
    public static final char XML_TAG_END = CHAR_GREATER_THAN;
    /**
     * The character that marks a tag as self-closing, {@code '/'}.
     */
    public static final char XML_SELF_CLOSING = CHAR_FORWARD_SLASH;
    /**
     * The character that opens and closes a prolog or processing instruction, {@code '?'}.
     */
    public static final char XML_PROLOG = CHAR_QUESTION_MARK;

    /**
     * The {@code "!--"} that opens a comment, right after the tag start.
     */
    public static final String XML_START_COMMENT = "!--";
    /**
     * The {@code "--"} that ends a comment, right before the tag end.
     */
    public static final String XML_END_COMMENT = "--";
    /**
     * The {@code "![CDATA["} that opens a CDATA section, right after the tag start.
     */
    public static final String XML_START_CDATA = "![CDATA[";
    /**
     * The {@code "]]"} that closes a CDATA section, right before the tag end.
     */
    public static final String XML_END_CDATA = "]]";

    /**
     * The empty string.
     */
    public static final String EMPTY = "";
    /**
     * A single space.
     */
    public static final String SPACE = " ";
    /**
     * Two spaces, the indentation added by one nesting level when pretty printing.
     */
    public static final String INDENT = "  ";
    /**
     * The {@code "<"} string.
     */
    public static final String LESS_THAN = "<";
    /**
     * The {@code ">"} string.
     */
    public static final String GREATER_THAN = ">";
    /**
     * The {@code "&"} string.
     */
    public static final String AMPERSAND = "&";
    /**
     * The {@code "="} string that separates an attribute name from its value.
     */
    public static final String EQUALS = "=";
    /**
     * The {@code "#"} that opens a numeric character reference, as in {@code "&#65;"}.
     */
    public static final String HASH = "#";
    /**
     * The {@code ";"} that closes a numeric character reference, as in {@code "&#65;"}.
     */
    public static final String SEMICOLON = ";";
    /**
     * A string holding one double quote.
     */
    public static final String DOUBLE_QUOTE = "\"";
    /**
     * A string holding one forward slash.
     */
    public static final String FORWARD_SLASH = "/";
    /**
     * A carriage return, {@code "\r"}.
     */
    public static final String CARRIAGE_RETURN = "\r";
    /**
     * A line feed, {@code "\n"}.
     */
    public static final String LINE_FEED = "\n";

    /**
     * The escaped less-than, {@code "&lt;"}.
     */
    public static final String ENCODED_LESS_THAN = "&lt;";
    /**
     * The escaped greater-than, {@code "&gt;"}.
     */
    public static final String ENCODED_GREATER_THAN = "&gt;";
    /**
     * The escaped single quote, {@code "&apos;"}.
     */
    public static final String ENCODED_SINGLE_QUOTE = "&apos;";
    /**
     * The escaped double quote, {@code "&quot;"}.
     */
    public static final String ENCODED_DOUBLE_QUOTE = "&quot;";
    /**
     * The escaped ampersand, {@code "&amp;"}.
     */
    public static final String ENCODED_AMPERSAND = "&amp;";
    /**
     * The {@code "&#"} that opens a numeric character reference.
     */
    public static final String ENCODED_UTF8 = "&#";

    /**
     * The {@code "["} that opens a predicate.
     */
    public static final String PREDICATE_START_SYMBOL = "[";
    /**
     * The {@code "]"} that closes a predicate.
     */
    public static final String PREDICATE_END_SYMBOL = "]";
    /**
     * The {@code "="} that separates the two sides of an equality predicate.
     */
    public static final String PREDICATE_EQUAL_SYMBOL = "=";
    /**
     * The {@code text()} segment, which selects the text of an element.
     */
    public static final String SEGMENT_EXPRESSION = "text()";
    /**
     * The {@code "/"} that separates the segments of an expression path.
     */
    public static final String EXPRESSION_PATH_SEPARATOR = "/";

    /**
     * The message for an equality predicate that does not have exactly two members.
     */
    public static final String ERROR_EQUALITY_WITHOUT_TWO_COMPONENTS = "Equality predicate must have exactly two members";
    /**
     * The message for an equality predicate that has an empty member.
     */
    public static final String ERROR_EQUALITY_WITH_EMPTY_PARTS = "Equality predicate must have two non-empty members";
    /**
     * The message for a predicate that does not start with {@code "["}.
     */
    public static final String ERROR_PREDICATE_WRONG_START = "Predicate does not start with [";
    /**
     * The message for a predicate that does not end with {@code "]"}.
     */
    public static final String ERROR_PREDICATE_WRONG_END = "Predicate does not end with ]";
    /**
     * The message for an element name that contains {@code "]"}.
     */
    public static final String ERROR_PREDICATE_WRONG_NAME = "Element name contains ]";
    /**
     * The message for a path that contains an empty segment.
     */
    public static final String ERROR_EMPTY_SEGMENT = "Path contains an empty segment";

    // This list contains named entities from HTML 2.0, HTML 3.2, HTML 4.0, HTML 5.0, XML 1.0, MathML 2.0,
    // and MathML 3.0.
    //
    // https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
    // https://www.freeformatter.com/html-entities.html
    //
    // Warning: The entities &DownBreve;, &tdot;, &TripleDot;, and &DotDot; had a leading space in MathML 2.0.
    // In MathML 3.0 and HTML 5.0 that leading space was removed. This map uses the newer version without the
    // leading space.
    //
    // Warning: It appears that HTML 3.2 allows named entities that do not end with a semicolon. Our parsing
    // does not support this.
    //
    // Warning: Our parsing uses Java chars to detect characters that need to be encoded. However, a Java char
    // contains only 16 bits, while some entities map onto characters that don't fit in 16 bits. It is therefore
    // not possible to encode those characters with the current implementation. HTML entities that fit this
    // definition are:
    //
    //    &acE;, &afr;, &Afr;, &Aopf;, &aopf;, &ascr;, &Ascr;, &bfr;, &Bfr;, &bne;, &bnequiv;, &Bopf;,
    //    &bopf;, &bscr;, &caps;, &cfr;, &copf;, &cscr;, &Cscr;, &cups;, &Dfr;, &dfr;, &Dopf;, &dopf;,
    //    &DotDot;, &DownBreve;, &dscr;, &Dscr;, &efr;, &Efr;, &eopf;, &Eopf;, &ffr;, &Ffr;, &fjlig;,
    //    &fopf;, &Fopf;, &fscr;, &gesl;, &Gfr;, &gfr;, &gopf;, &Gopf;, &Gscr;, &gvertneqq;, &gvnE;,
    //    &hfr;, &hopf;, &hscr;, &ifr;, &Iopf;, &iopf;, &iscr;, &jfr;, &Jfr;, &Jopf;, &jopf;, &jscr;,
    //    &Jscr;, &Kfr;, &kfr;, &Kopf;, &kopf;, &kscr;, &Kscr;, &lates;, &lesg;, &lfr;, &Lfr;, &Lopf;,
    //    &lopf;, &lscr;, &lvertneqq;, &lvnE;, &mfr;, &Mfr;, &mopf;, &Mopf;, &mscr;, &nang;, &napE;,
    //    &napid;, &nbump;, &nbumpe;, &ncongdot;, &nedot;, &nesim;, &nfr;, &Nfr;, &ngE;, &ngeqq;,
    //    &ngeqslant;, &nges;, &nGg;, &nGt;, &nGtv;, &nlE;, &nleqq;, &nleqslant;, &nles;, &nLl;, &nLt;,
    //    &nLtv;, &nopf;, &NotGreaterFullEqual;, &NotGreaterGreater;, &NotGreaterSlantEqual;,
    //    &NotHumpDownHump;, &NotHumpEqual;, &notindot;, &notinE;, &NotLeftTriangleBar;, &NotLessLess;,
    //    &NotLessSlantEqual;, &NotNestedGreaterGreater;, &NotNestedLessLess;, &NotPrecedesEqual;,
    //    &NotRightTriangleBar;, &NotSquareSubset;, &NotSquareSuperset;, &NotSubset;, &NotSucceedsEqual;,
    //    &NotSucceedsTilde;, &NotSuperset;, &nparsl;, &npart;, &npre;, &npreceq;, &nrarrc;, &nrarrw;,
    //    &nsce;, &Nscr;, &nscr;, &nsubE;, &nsubset;, &nsubseteqq;, &nsucceq;, &nsupE;, &nsupset;,
    //    &nsupseteqq;, &nvap;, &nvge;, &nvgt;, &nvle;, &nvlt;, &nvltrie;, &nvrtrie;, &nvsim;, &Ofr;,
    //    &ofr;, &oopf;, &Oopf;, &Oscr;, &pfr;, &Pfr;, &popf;, &Pscr;, &pscr;, &qfr;, &Qfr;, &qopf;,
    //    &qscr;, &Qscr;, &race;, &rfr;, &ropf;, &rscr;, &sfr;, &Sfr;, &smtes;, &Sopf;, &sopf;, &sqcaps;,
    //    &sqcups;, &sscr;, &Sscr;, &tdot;, &tfr;, &Tfr;, &ThickSpace;, &Topf;, &topf;, &TripleDot;,
    //    &tscr;, &Tscr;, &ufr;, &Ufr;, &uopf;, &Uopf;, &Uscr;, &uscr;, &varsubsetneq;, &varsubsetneqq;,
    //    &varsupsetneq;, &varsupsetneqq;, &Vfr;, &vfr;, &vnsub;, &vnsup;, &vopf;, &Vopf;, &Vscr;, &vscr;,
    //    &vsubnE;, &vsupne;, &vsupnE;, &wfr;, &Wfr;, &wopf;, &Wopf;, &Wscr;, &wscr;, &xfr;, &Xfr;, &xopf;,
    //    &Xopf;, &Xscr;, &xscr;, &yfr;, &Yfr;, &Yopf;, &yopf;, &yscr;, &Yscr;, &zfr;, &zopf;, &zscr;, &Zscr;
    //
    /**
     * Maps an entity name, such as {@code "&amp;"}, to the characters it stands for; read from
     * a resource in the jar and used to decode entities.
     */
    public static final Map<String, String> NAMED_ENTITIES = loadNamedEntitiesMap();
    /**
     * Maps a single character to the shortest entity name for it, the direction needed to
     * encode text back into entities.
     */
    public static final Map<Character, String> REVERSE_ENTITIES = loadReverseEntities(NAMED_ENTITIES);

    private static Map<String, String> loadNamedEntitiesMap() {
        final var hexFormat = HexFormat.of();
        final var map = new LinkedHashMap<String, String>();

        final var in = Constants.class.getResourceAsStream("/xmlparser/named-entities-map");
        if (in == null) throw new IllegalStateException("Missing named entities map");

        try (final var reader = new BufferedReader(new InputStreamReader(in))) {
            int i = 0;
            String line; while ((line = reader.readLine()) != null) {
                i++;
                if (line.isEmpty()) continue;

                final int offset = line.indexOf(';');
                final var key = line.substring(0, offset + 1);
                final var value = new String(hexFormat.parseHex(line.substring(offset+1)), UTF_8);
                if (key.isBlank()) System.out.println("line " + i);
                map.put(key, value);
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Failure during named entities map parsing", e);
        }

        return map;
    }

    private static Map<Character, String> loadReverseEntities(final Map<String, String> entities) {
        final var map = new HashMap<Character, String>();

        for (final var entry : entities.entrySet()) {
            final var entityName = entry.getKey();
            final var entityChar = entry.getValue();

    // It <i>may</i> be possible to use String.length(), but I don't want to risk it. Our parser uses
    // `toCharArray()` to split a String so that's what I'm sticking with
            if (entityChar.toCharArray().length > 1) continue;

    // Grab the smallest entityName that is available, no need to make big data
            map.compute(entityChar.charAt(0), (c, storedName)
                    -> storedName == null ? entityName
                    : entityName.length() >= storedName.length() ? storedName
                    : entityName);
        }

        return map;
    }

}
