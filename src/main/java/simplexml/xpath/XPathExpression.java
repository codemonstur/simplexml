package simplexml.xpath;

import simplexml.error.InvalidXPath;
import simplexml.model.Element;

import java.util.*;

import static simplexml.utils.Builder.toNumber;
import static simplexml.utils.Constants.EXPRESSION_PATH_SEPARATOR;

/**
 * Performs queries on XML documents. The queries are written using the XPath syntax, with the following restrictions:
 * <ul>
 * <li>Transitive children (<tt>//</tt>), parent (<tt>../</tt>) and
 * <tt>sibling</tt> axes are not supported</li>
 * <li>Attributes (<tt>@att</tt>) are not supported</li>
 * <li>The only operator allowed in a predicate is equality between a path and a constant</li>
 * </ul>
 * Normal usage involves instantiating an expression from a String using the {@link #toXPathExpression(String)} method, and
 * then querying a document using the evaluate method.
 * <p>
 * Examples of valid queries:
 * <ol>
 * <li><tt>abc/def</tt></li>
 * <li><tt>abc[ghi=3]/def/text()</tt></li>
 * <li><tt>abc[ghi=3][q=0]/def[xyz='hello']</tt></li>
 * </ol>
 */ 
public final class XPathExpression {

	public final List<Segment> segments;
	public XPathExpression(final List<Segment> segments) {
		this.segments = segments;
	}
	
	public static XPathExpression toXPathExpression(final String s) throws InvalidXPath {
		if (s == null) throw new InvalidXPath("Input string is null");

		final List<Segment> segments = new ArrayList<>();
		for (final String part : s.split(EXPRESSION_PATH_SEPARATOR)) {
			segments.add(Segment.parseSegment(part));
		}
		return new XPathExpression(segments);
	}

	public Object evaluateAny(final Element root) {
		for (final Object e : evaluate(root)) {
			return e;
		}
		return null;
	}

	public String evaluateAnyString(final Element root) {
		for (final Object e : evaluate(root)) {
			if (e instanceof String)
				return (String) e;
		}
		return "";
	}

	public Number evaluateAnyNumber(final Element root) {
		for (Object e : evaluate(root)) {
			if (e instanceof String) {
				final Number n = toNumber((String)e);
				if (n != null) return n;
			}
		}
		return null;
	}

	public int evaluateAnyInt(final Element root) {
		final Number n = evaluateAnyNumber(root);
		return n == null ? 0 : n.intValue();
	}

	public float evaluateAnyFloat(final Element root) {
		final Number n = evaluateAnyNumber(root);
		return n == null ? 0 : n.floatValue();
	}

	public Set<String> evaluateAsStrings(final Element root) {
		final Set<String> new_col = new HashSet<>();
		for (final Object e : evaluate(root)) {
			if (e instanceof String) {
				new_col.add((String)e);
			}
		}
		return new_col;
	}

	public Set<Number> evaluateAsNumbers(final Element root) {
		final Set<Number> new_col = new HashSet<>();
		for (final Object e : evaluate(root)) {
			if (e instanceof String) {
				final Number n = toNumber((String)e);
				if (n != null) new_col.add(n);
			}
		}
		return new_col;
	}


	public Set<Object> evaluate(final Element root) {
		return evaluate(segments, root);
	}

	private static Set<Object> evaluate(final List<Segment> segments, final Object root) {
		final Set<Object> result = new HashSet<>();

		final Segment first_segment = segments.get(0);
		if (first_segment instanceof TextSegment) {
			if (root instanceof String) {
				result.add(root);
			}
			return result;
		}

		if (root instanceof Element) {
			final Element r = (Element) root;
			if (first_segment.elementName.equals(r.name)) {
				return result;
			}

			final Collection<Predicate> predicates = first_segment.predicates;
			for (final Predicate p : predicates) {
				if (!p.evaluate(r)) {
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
				for (final Element child : r.children) {
					result.addAll(evaluate(new_segments, child));
				}
				if (r.text != null) {
					result.addAll(evaluate(new_segments, r.text));
				}
			}
			else {
				result.add(root);
			}
		}
		return result;
	}

}
