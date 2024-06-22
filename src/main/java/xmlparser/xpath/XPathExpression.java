package xmlparser.xpath;

import xmlparser.error.InvalidXPath;
import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;

import java.util.*;

import static xmlparser.utils.Builder.toNumber;
import static xmlparser.utils.Constants.EXPRESSION_PATH_SEPARATOR;

public final class XPathExpression {

	public final List<Segment> segments;
	public XPathExpression(final List<Segment> segments) {
		this.segments = segments;
	}

    public static XPathExpression newXPath(final String xpath) throws InvalidXPath {
        if (xpath == null) throw new InvalidXPath("Input string is null");

        final List<Segment> segments = new ArrayList<>();
        for (final String part : xpath.split(EXPRESSION_PATH_SEPARATOR)) {
            segments.add(Segment.parseSegment(part));
        }
        return new XPathExpression(segments);
    }

    public XmlElement evaluateAny(final XmlElement root) {
        return evaluate(root).stream().findFirst().orElse(null);
    }

    public String evaluateAnyString(final XmlElement root) {
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement)
                return ((XmlTextElement) e).text;
        }
        return "";
    }

    public Number evaluateAnyNumber(final XmlElement root) {
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement) {
                final Number n = toNumber(((XmlTextElement) e).text);
                if (n != null) return n;
            }
        }
        return null;
    }

    public int evaluateAnyInt(final XmlElement root) {
        final Number n = evaluateAnyNumber(root);
        return n == null ? 0 : n.intValue();
    }

    public float evaluateAnyFloat(final XmlElement root) {
        final Number n = evaluateAnyNumber(root);
        return n == null ? 0 : n.floatValue();
    }

    public Set<String> evaluateAsStrings(final XmlElement root) {
        final Set<String> new_col = new HashSet<>();
        for (final XmlElement e : evaluate(root)) {
            if (e instanceof XmlTextElement) {
                new_col.add(((XmlTextElement) e).text);
            }
        }
        return new_col;
    }

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
