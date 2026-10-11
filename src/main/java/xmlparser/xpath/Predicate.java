package xmlparser.xpath;

import xmlparser.error.InvalidXPath;
import xmlparser.model.XmlElement;

import static xmlparser.model.XmlElement.findChildForName;
import static xmlparser.utils.Constants.*;
import static xmlparser.utils.Validator.hasExactLength;
import static xmlparser.utils.Validator.partsAreNotEmpty;

/**
 * Represents a predicate that can be evaluated against an XmlElement.
 */
public interface Predicate {

	/**
	 * Performs the actual evaluation.
	 * @param root The root tag to start the evaluation from
	 * @return the result of the evaluation
	 */
	boolean evaluate(XmlElement root);

	/**
	 * Reads one predicate out of a step. Only an equality like {@code name=value} is understood;
	 * any other shape is reported rather than turned into a predicate that never matches.
	 * @param predicate the predicate text, without the brackets around it
	 * @return the predicate to evaluate against an element
	 * @throws InvalidXPath if the predicate is not an equality of two non-empty parts
	 */
	static Predicate parsePredicate(final String predicate) throws InvalidXPath {
		if (predicate.contains(PREDICATE_EQUAL_SYMBOL)) return newEqualityPredicate(predicate);
		throw new InvalidXPath("Could not parse predicate " + predicate);
	}

	/**
	 * Builds an equality predicate: it looks for a child of the evaluated element carrying the
	 * name on the left of the '=' and compares that child's text with the right side.
	 * @param s the predicate in the form {@code name=value}
	 * @return a predicate that is true only when such a child holds exactly the expected text
	 * @throws InvalidXPath if the predicate does not split into exactly two non-empty parts
	 */
	static Predicate newEqualityPredicate(final String s) throws InvalidXPath {
		final String[] parts = partsAreNotEmpty(hasExactLength(s.split(PREDICATE_EQUAL_SYMBOL), 2, ERROR_EQUALITY_WITHOUT_TWO_COMPONENTS), ERROR_EQUALITY_WITH_EMPTY_PARTS);
		return root -> {
			final XmlElement el = findChildForName(root, parts[0], null);
			return el != null && parts[1].equals(el.getText());
		};
	}

}
