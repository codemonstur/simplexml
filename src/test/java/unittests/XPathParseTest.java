package unittests;

import org.junit.Test;
import xmlparser.error.InvalidXPath;
import xmlparser.xpath.Predicate;
import xmlparser.xpath.Segment;
import xmlparser.xpath.TextSegment;
import xmlparser.xpath.XPathExpression;

import static org.junit.Assert.*;

public class XPathParseTest {

	@Test
	public void testEmpty() {
		final var actual = XPathExpression.newXPath("");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());
		assertNotNull(actual.segments.get(0));
		assertEquals("", actual.segments.get(0).elementName);
	}

	@Test
	public void testSingleElement() {
		final var actual = XPathExpression.newXPath("a");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());
		assertNotNull(actual.segments.get(0));
		assertEquals("a", actual.segments.get(0).elementName);
	}
	
	@Test
	public void testTwoElements() {
		final var actual = XPathExpression.newXPath("foo/bar");

		assertNotNull(actual);
		assertEquals(2, actual.segments.size());
		assertNotNull(actual.segments.get(0));
		assertEquals("foo", actual.segments.get(0).elementName);
		assertNotNull(actual.segments.get(1));
		assertEquals("bar", actual.segments.get(1).elementName);
	}
	
	@Test
	public void testElementPredicate() {
		final var actual = XPathExpression.newXPath("foo[a=0]");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());

		final var actualSegment = actual.segments.get(0);
		assertNotNull(actualSegment);
		assertEquals("foo", actualSegment.elementName);
		assertEquals(1, actualSegment.predicates.size());
		for (final var p : actualSegment.predicates)
			assertNotNull(p);
	}
	
	@Test
	public void testSinglePredicate() {
		final var actual = XPathExpression.newXPath("[a=0]");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());

		final var actualSegment = actual.segments.get(0);
		assertNotNull(actualSegment);
		assertEquals("", actualSegment.elementName);
		assertEquals(1, actualSegment.predicates.size());
		for (final var p : actualSegment.predicates)
			assertNotNull(p);
	}
	
	@Test
	public void testTwoPredicates() {
		final var actual = XPathExpression.newXPath("[a=0][b=1]");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());

		final var actualSegment = actual.segments.get(0);
		assertNotNull(actualSegment);
		assertEquals("", actualSegment.elementName);
		assertEquals(2, actualSegment.predicates.size());
		for (final var p : actualSegment.predicates)
			assertNotNull(p);
	}
	
	@Test
	public void testTwoPredicatesSpace() {
		final var actual = XPathExpression.newXPath("[a=0] [b=1]");
		assertNotNull(actual);
		assertEquals(1, actual.segments.size());

		final var actualSegment = actual.segments.get(0);
		assertNotNull(actualSegment);
		assertEquals("", actualSegment.elementName);
		assertEquals(2, actualSegment.predicates.size());
		for (final var p : actualSegment.predicates)
			assertNotNull(p);
	}
	
	@Test
	public void testTextSegment1() {
		final var actual = XPathExpression.newXPath("text()");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());
		assertNotNull(actual.segments.get(0));
		assertTrue(actual.segments.get(0) instanceof TextSegment);
		assertEquals("text()", actual.segments.get(0).toString());
	}
	
	@Test
	public void testTextSegment2() {
		final var actual = XPathExpression.newXPath("foo/text()");

		assertNotNull(actual);
		assertEquals(2, actual.segments.size());
		assertNotNull(actual.segments.get(1));
		assertTrue(actual.segments.get(1) instanceof TextSegment);
		assertEquals("text()", actual.segments.get(1).toString());
	}

	@Test(expected = InvalidXPath.class)
	public void testNull() {
		XPathExpression.newXPath(null);

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed1() {
		XPathExpression.newXPath("[");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed2() {
		XPathExpression.newXPath("a[");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed3() {
		XPathExpression.newXPath("[]");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed4() {
		XPathExpression.newXPath("a]");

		fail("Should throw an exception");
	}
	
	@Test(expected = InvalidXPath.class)
	public void testMalformed5() {
		XPathExpression.newXPath("abc[a=1]][b=0]");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed6() {
		XPathExpression.newXPath("ab]cd[foo=0]");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformedEquality1() {
		XPathExpression.newXPath("a[=]");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformedEquality2() {
		XPathExpression.newXPath("a[=c]");

		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformedEquality3() {
		XPathExpression.newXPath("a[c=]");

		fail("Should throw an exception");
	}
	
}
