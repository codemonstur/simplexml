package simplexml.xpath;

import simplexml.error.InvalidXPath;
import simplexml.model.Element;

import static simplexml.model.Element.findChildForName;
import static simplexml.utils.Constants.*;
import static simplexml.utils.Validator.hasExactLength;
import static simplexml.utils.Validator.partsAreNotEmpty;

public interface Predicate {

	boolean evaluate(Element root);

	static Predicate parsePredicate(final String predicate) throws InvalidXPath {
		if (predicate.contains(PREDICATE_EQUAL_SYMBOL)) return newEqualityPredicate(predicate);
		throw new InvalidXPath("Could not parse predicate " + predicate);
	}

	static Predicate newEqualityPredicate(final String s) throws InvalidXPath {
		final String[] parts = partsAreNotEmpty(hasExactLength(s.split(PREDICATE_EQUAL_SYMBOL), 2, ERROR_EQUALITY_WITHOUT_TWO_COMPONENTS), ERROR_EQUALITY_WITH_EMPTY_PARTS);
		return root -> {
			final Element el = findChildForName(root, parts[0], null);
			return el != null && parts[1].equals(el.text);
		};
	}

}
