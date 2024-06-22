package xmlparser.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public enum Constants {;

    public static final char
        CHAR_FORWARD_SLASH = '/',
        CHAR_SPACE = ' ',
        CHAR_EQUALS = '=',
        CHAR_LESS_THAN = '<',
        CHAR_GREATER_THAN = '>',
        CHAR_QUESTION_MARK = '?',
        CHAR_SINGLE_QUOTE = '\'',
        CHAR_DOUBLE_QUOTE = '"',
        CHAR_AMPERSAND = '&';

    public static final char
        XML_TAG_START = CHAR_LESS_THAN,
        XML_TAG_END = CHAR_GREATER_THAN,
        XML_SELF_CLOSING = CHAR_FORWARD_SLASH,
        XML_PROLOG = CHAR_QUESTION_MARK;

    public static final String
        XML_START_COMMENT = "!--",
        XML_END_COMMENT = "--";

    public static final String
        EMPTY = "",
        SPACE = " ",
        INDENT = "  ",
        LESS_THAN = "<",
        GREATER_THAN = ">",
        AMPERSAND = "&",
        EQUALS = "=",
        HASH = "#",
        SEMICOLON = ";",
        DOUBLE_QUOTE = "\"",
        FORWARD_SLASH = "/",
        CARRIAGE_RETURN = "\r",
        LINE_FEED = "\n";

    public static final String
        ENCODED_LESS_THAN = "&lt;",
        ENCODED_GREATER_THAN = "&gt;",
        ENCODED_SINGLE_QUOTE = "&apos;",
        ENCODED_DOUBLE_QUOTE = "&quot;",
        ENCODED_AMPERSAND = "&amp;",
        ENCODED_UTF8 = "&#";

    public static final String
        PREDICATE_START_SYMBOL = "[",
        PREDICATE_END_SYMBOL = "]",
        PREDICATE_EQUAL_SYMBOL = "=",
        SEGMENT_EXPRESSION = "text()",
        EXPRESSION_PATH_SEPARATOR = "/";

    public static final String
        ERROR_EQUALITY_WITHOUT_TWO_COMPONENTS = "Equality predicate must have exactly two members",
        ERROR_EQUALITY_WITH_EMPTY_PARTS = "Equality predicate must have two non-empty members",
        ERROR_PREDICATE_WRONG_START = "Predicate does not start with [",
        ERROR_PREDICATE_WRONG_END = "Predicate does not end with ]",
        ERROR_PREDICATE_WRONG_NAME = "Element name contains ]";

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
    public static final Map<String, String> NAMED_ENTITIES = loadNamedEntitiesMap();
    public static final Map<Character, String> REVERSE_ENTITIES = loadReverseEntities(NAMED_ENTITIES);

    private static Map<String, String> loadNamedEntitiesMap() {
        final var hexFormat = HexFormat.of();
        final var map = new LinkedHashMap<String, String>();

        final var in = Constants.class.getResourceAsStream("/named-entities-map");
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
