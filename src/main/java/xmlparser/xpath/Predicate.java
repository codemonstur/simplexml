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

	static Predicate parsePredicate(final String predicate) throws InvalidXPath {
		if (predicate.contains(PREDICATE_EQUAL_SYMBOL)) return newEqualityPredicate(predicate);
		throw new InvalidXPath("Could not parse predicate " + predicate);
	}

	static Predicate newEqualityPredicate(final String s) throws InvalidXPath {
		final String[] parts = partsAreNotEmpty(hasExactLength(s.split(PREDICATE_EQUAL_SYMBOL), 2, ERROR_EQUALITY_WITHOUT_TWO_COMPONENTS), ERROR_EQUALITY_WITH_EMPTY_PARTS);
		return root -> {
			final XmlElement el = findChildForName(root, parts[0], null);
			return el != null && parts[1].equals(el.getText());
		};
	}

}
