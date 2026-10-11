package xmlparser.xpath;

import xmlparser.error.InvalidXPath;
import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;

import java.util.*;

import static xmlparser.utils.Builder.toNumber;
import static xmlparser.utils.Constants.EMPTY;
import static xmlparser.utils.Constants.ERROR_EMPTY_SEGMENT;
import static xmlparser.utils.Constants.EXPRESSION_PATH_SEPARATOR;

/**
 * A parsed XPath: the steps to walk down, starting from the root of whatever element it is
 * evaluated against. Make one with {@link #newXPath(String)} and then ask for what it found, as
 * elements, as text, or as numbers.
 */
public final class XPathExpression {

	/**
	 * The steps to walk, in the order they appear in the expression.
	 */
	public final List<Segment> segments;
	/**
	 * Wraps steps that were parsed elsewhere, kept in the list exactly as given.
	 * @param segments the steps to walk, in order
	 */
	public XPathExpression(final List<Segment> segments) {
		this.segments = segments;
	}

    /**
     * Parses an expression into its steps. A leading separator is accepted and ignored, while any
     * other empty step, left behind by a '//' or a trailing '/', is rejected rather than ignored.
     * @param xpath the expression to parse, which may be empty
     * @return the parsed expression, which holds at least one step; an empty path gives one with
     *         an empty name
     * @throws InvalidXPath if the expression is null or holds an empty step
     */
    public static XPathExpression newXPath(final String xpath) throws InvalidXPath {
        if (xpath == null) throw new InvalidXPath("Input string is null");

        // A leading separator makes the path absolute, which is free to honour because evaluation
        // always starts at the root. Any other empty segment is a '//' or a trailing '/', neither
        // of which this engine supports, so it is reported instead of never matching anything.
        final String path = xpath.startsWith(EXPRESSION_PATH_SEPARATOR)
            ? xpath.substring(EXPRESSION_PATH_SEPARATOR.length())
            : xpath;

        final List<Segment> segments = new ArrayList<>();
        if (!path.isEmpty()) {
            for (final String part : path.split(EXPRESSION_PATH_SEPARATOR, -1)) {
                if (part.isEmpty()) throw new InvalidXPath(ERROR_EMPTY_SEGMENT + ": " + xpath);
                segments.add(Segment.parseSegment(part));
            }
        }
        if (segments.isEmpty()) segments.add(Segment.parseSegment(EMPTY));

        return new XPathExpression(segments);
    }

    /**
     * Walks the steps and takes whichever match comes first out of the set they land in, so which
     * one it is cannot be relied on.
     * @param root the element to start from; a null root matches nothing
     * @return the first match, or null when nothing matches
     */
    public XmlElement evaluateAny(final XmlElement root) {
        return evaluate(root).stream().findFirst().orElse(null);
    }

    /**
     * Reads the text out of whichever match holds text first, again in the order the set happens
     * to give them, skipping matches that are not text nodes.
     * @param root the element to start from; a null root matches nothing
     * @return the text of the first text node among the matches, or "" when there is none
     */
    public String evaluateAnyString(final XmlElement root) {
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement)
                return ((XmlTextElement) e).text;
        }
        return "";
    }

    /**
     * Reads the first number among the matches, skipping text that does not parse as one.
     * @param root the element to start from; a null root matches nothing
     * @return the value as a Long when it is integral and as a Double when it has a decimal part,
     *         or null when no match holds a number
     */
    public Number evaluateAnyNumber(final XmlElement root) {
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement) {
                final Number n = toNumber(((XmlTextElement) e).text);
                if (n != null) return n;
            }
        }
        return null;
    }

    /**
     * Reads the first number among the matches the way {@link #evaluateAnyNumber} finds it, then
     * narrows it to an int.
     * @param root the element to start from; a null root matches nothing
     * @return that number as an int, or 0 when no match holds a number
     */
    public int evaluateAnyInt(final XmlElement root) {
        final Number n = evaluateAnyNumber(root);
        return n == null ? 0 : n.intValue();
    }

    /**
     * Reads the first number among the matches the way {@link #evaluateAnyNumber} finds it, then
     * narrows it to a float.
     * @param root the element to start from; a null root matches nothing
     * @return that number as a float, or 0 when no match holds a number
     */
    public float evaluateAnyFloat(final XmlElement root) {
        final Number n = evaluateAnyNumber(root);
        return n == null ? 0 : n.floatValue();
    }

    /**
     * Collects the text of every match that is a text node, so texts that repeat are held once.
     * @param root the element to start from; a null root matches nothing
     * @return the texts, empty when no match holds text
     */
    public Set<String> evaluateAsStrings(final XmlElement root) {
        final Set<String> new_col = new HashSet<>();
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement) {
                new_col.add(((XmlTextElement) e).text);
            }
        }
        return new_col;
    }

    /**
     * Collects the numbers out of every match, skipping text that does not parse as one.
     * @param root the element to start from; a null root matches nothing
     * @return the values, as Longs and Doubles, empty when no match holds a number
     */
    public Set<Number> evaluateAsNumbers(final XmlElement root) {
        final Set<Number> new_col = new HashSet<>();
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement) {
                final Number n = toNumber(((XmlTextElement) e).text);
                if (n != null) new_col.add(n);
            }
        }
        return new_col;
    }


    /**
     * Walks the steps down from the root through its children, keeping only the elements that
     * have the name a step asks for and satisfy all of that step's predicates.
     * @param root the element to start from, which may be a text node; a null root matches nothing
     * @return every element the walk reaches, empty when nothing matches
     */
    public Set<XmlElement> evaluate(final XmlElement root) {
        return evaluate(segments, root);
    }

	private static Set<XmlElement> evaluate(final List<Segment> segments, final XmlElement root) {
        final Set<XmlElement> result = new HashSet<>();

        final Segment first_segment = segments.get(0);
        if (first_segment instanceof TextSegment) {
            if (root instanceof XmlTextElement) {
                result.add(root);
            }
            return result;
        }

        // Text nodes can never match an element segment, and they have no name to compare against
        if (root == null || root instanceof XmlTextElement) {
            return result;
        }

        if (first_segment.elementName.compareTo(root.name) != 0) {
            return result;
        }

        final Collection<Predicate> predicates = first_segment.predicates;
        for (final Predicate p : predicates) {
            if (!p.evaluate(root)) {
                // Predicate returns false: stop considering this branch
                return result;
            }
        }

        // This segment is OK; remove it and continue evaluation with every
        // child of the root
        if (segments.size() == 1) {
            result.add(root);
            return result;
        }

        final List<Segment> new_segments = segments.subList(1, segments.size());
        if (!new_segments.isEmpty()) {
            for (final XmlElement child : root.children) {
                result.addAll(evaluate(new_segments, child));
            }
        }
        else {
            result.add(root);
        }
        return result;
	}

}
