package xmlparser.xpath;

import static xmlparser.utils.Constants.SEGMENT_EXPRESSION;

/**
 * The text() step of an XPath, which matches a text node rather than an element and so has no
 * name of its own.
 */
public class TextSegment extends Segment {

	/** Builds the text() step, which matches a text node and so has no name. */
	public TextSegment() { }

	@Override
	public String toString() {
		return SEGMENT_EXPRESSION;
	}

}
