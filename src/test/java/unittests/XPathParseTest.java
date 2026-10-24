package unittests;

import org.junit.jupiter.api.Test;
import xmlparser.error.InvalidXPath;
import xmlparser.xpath.TextSegment;
import xmlparser.xpath.XPathExpression;

import static org.junit.jupiter.api.Assertions.*;

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
        assertInstanceOf(TextSegment.class, actual.segments.get(0));
		assertEquals("text()", actual.segments.get(0).toString());
	}
	
	@Test
	public void testTextSegment2() {
		final var actual = XPathExpression.newXPath("foo/text()");

		assertNotNull(actual);
		assertEquals(2, actual.segments.size());
		assertNotNull(actual.segments.get(1));
        assertInstanceOf(TextSegment.class, actual.segments.get(1));
		assertEquals("text()", actual.segments.get(1).toString());
	}

	@Test
	public void testNull() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath(null));
	}

	@Test
	public void testMalformed1() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("["));
	}

	@Test
	public void testMalformed2() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("a["));
	}

	@Test
	public void testMalformed3() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("[]"));
	}

	@Test
	public void testMalformed4() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("a]"));
	}
	
	@Test
	public void testMalformed5() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("abc[a=1]][b=0]"));
	}

	@Test
	public void testMalformed6() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("ab]cd[foo=0]"));
	}

	@Test
	public void testMalformedEquality1() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("a[=]"));
	}

	@Test
	public void testMalformedEquality2() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("a[=c]"));
	}

	@Test
	public void testMalformedEquality3() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("a[c=]"));
	}

	@Test
	public void testAbsolutePath() {
		final var actual = XPathExpression.newXPath("/foo/bar");

		assertNotNull(actual);
		assertEquals(2, actual.segments.size());
		assertNotNull(actual.segments.get(0));
		assertEquals("foo", actual.segments.get(0).elementName);
		assertNotNull(actual.segments.get(1));
		assertEquals("bar", actual.segments.get(1).elementName);
	}

	@Test
	public void testAbsoluteTextSegment() {
		final var actual = XPathExpression.newXPath("/foo/text()");

		assertEquals(2, actual.segments.size());
		assertNotNull(actual.segments.get(1));
        assertInstanceOf(TextSegment.class, actual.segments.get(1));
	}

	@Test
	public void testSingleSlashIsTheWholePath() {
		final var actual = XPathExpression.newXPath("/");

		assertNotNull(actual);
		assertEquals(1, actual.segments.size());
		assertEquals("", actual.segments.get(0).elementName);
	}

	@Test
	public void testDoubleSlash() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("foo//bar"),
			"Empty segment not detected");
	}

	@Test
	public void testLeadingDoubleSlash() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("//foo"),
			"Empty segment not detected");
	}

	@Test
	public void testTrailingSlash() {
		assertThrows(InvalidXPath.class, () ->
			XPathExpression.newXPath("foo/"),
			"Empty segment not detected");
	}

	@Test
	public void testEmptySegmentReportsTheWholePath() {
		try {
			XPathExpression.newXPath("foo//bar");
			fail("Empty segment not detected");
		} catch (final InvalidXPath e) {
			assertEquals("Path contains an empty segment: foo//bar", e.getMessage(), "Invalid error message");
		}
	}

}
