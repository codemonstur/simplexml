package xmlparser.xpath;

import xmlparser.error.InvalidXPath;

import java.util.ArrayList;
import java.util.List;

import static xmlparser.utils.Constants.*;
import static xmlparser.xpath.Predicate.parsePredicate;

/**
 * One step of an XPath: the name of the element to match, and the predicates that element has to
 * satisfy before the walk continues down through it.
 */
public class Segment {

	/**
	 * The name of the element to match, empty for a step that has no name of its own.
	 */
	public final String elementName;
	/**
	 * The conditions the element has to satisfy, or null when the step was built without any.
	 */
	public final List<Predicate> predicates;

	/**
	 * Builds a step with an empty name and no predicates at all.
	 */
	public Segment() {
		this("", null);
	}

	/**
	 * Builds a step from parts that were parsed elsewhere, kept exactly as given.
	 * @param elementName the name of the element to match, empty for a step without one
	 * @param predicates the conditions to satisfy, or null for none
	 */
	public Segment(final String elementName, final List<Predicate> predicates) {
		this.elementName = elementName;
		this.predicates = predicates;
	}


    /**
     * Parses one step of a path: the element name up to the first '[', then every bracketed
     * predicate that follows the name. A step reading {@code text()} gives a {@link TextSegment}.
     * @param segment the step to parse, with no separator in it
     * @return the parsed step
     * @throws InvalidXPath if the name holds a ']', a predicate does not open with '[' or close
     *         with ']', or a predicate is not an equality of two non-empty parts
     */
    public static Segment parseSegment(final String segment) throws InvalidXPath {
		final String elementName = isValidElementName(segment.substring(0, indexOfPredicateSection(segment)));
        if (SEGMENT_EXPRESSION.equals(elementName)) return new TextSegment();

		return new Segment(elementName, extractPredicates(segment.substring(elementName.length())));
	}

    private static int indexOfPredicateSection(final String segment) {
        final int offsetPredicateStart = segment.indexOf(PREDICATE_START_SYMBOL);
        return offsetPredicateStart == -1 ? segment.length() : offsetPredicateStart;
	}

    private static String isValidElementName(final String elementName) throws InvalidXPath {
        if (elementName.contains(PREDICATE_END_SYMBOL)) throw new InvalidXPath(ERROR_PREDICATE_WRONG_NAME);
        return elementName;
    }

    private static List<Predicate> extractPredicates(String segmentPart) throws InvalidXPath {
        final List<Predicate> predicates = new ArrayList<>();

        while (!segmentPart.isEmpty()) {
            if (!segmentPart.startsWith(PREDICATE_START_SYMBOL))
                throw new InvalidXPath(ERROR_PREDICATE_WRONG_START);

            final String predicate = extractPredicate(segmentPart);
            predicates.add(parsePredicate(predicate.trim()));

            // need to read over the ] symbol
            segmentPart = segmentPart.substring(predicate.length() + 2).trim();
        }

        return predicates;
    }

    private static String extractPredicate(final String segmentPart) throws InvalidXPath {
        final int offsetClosingSymbol = segmentPart.indexOf(PREDICATE_END_SYMBOL);
        if (offsetClosingSymbol < 0) throw new InvalidXPath(ERROR_PREDICATE_WRONG_END);
        return segmentPart.substring(1, offsetClosingSymbol);
    }

}
