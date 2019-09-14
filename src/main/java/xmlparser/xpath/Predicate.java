package xmlparser.xpath;

import xmlparser.error.InvalidXPath;
import xmlparser.model.XmlElement;

import static xmlparser.model.XmlElement.findChildForName;
import static xmlparser.utils.Constants.*;
import static xmlparser.utils.Validator.hasExactLength;
import static xmlparser.utils.Validator.partsAreNotEmpty;

/**
 * Adapted from xml-lif (https://github.com/liflab/xml-lif) by Sylvain Hallé
 */
public interface Predicate {

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
