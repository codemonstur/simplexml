package xmlparser.xpath;

import xmlparser.error.InvalidXPath;

import java.util.ArrayList;
import java.util.List;

import static xmlparser.utils.Constants.*;
import static xmlparser.xpath.Predicate.parsePredicate;

/**
 * Adapted from xml-lif (https://github.com/liflab/xml-lif) by Sylvain Hallé
 */
public class Segment {

	public final String elementName;
	public final List<Predicate> predicates;

	public Segment() {
		this("", null);
	}

	public Segment(final String elementName, final List<Predicate> predicates) {
		this.elementName = elementName;
		this.predicates = predicates;
	}


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
