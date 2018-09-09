package unittests;

import org.junit.Test;
import simplexml.error.InvalidXPath;
import simplexml.xpath.Predicate;
import simplexml.xpath.Segment;
import simplexml.xpath.TextSegment;
import simplexml.xpath.XPathExpression;

import static org.junit.Assert.*;

public class XPathParseTest {

	@Test
	public void testEmpty() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("", seg.elementName);
	}

	@Test
	public void testSingleElement() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("a");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("a", seg.elementName);
	}
	
	@Test
	public void testTwoElements() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("foo/bar");
		assertNotNull(xpe);
		assertEquals(2, xpe.segments.size());
		{
			Segment seg = xpe.segments.get(0);
			assertNotNull(seg);
			assertEquals("foo", seg.elementName);
		}
		{
			Segment seg = xpe.segments.get(1);
			assertNotNull(seg);
			assertEquals("bar", seg.elementName);
		}
	}
	
	@Test
	public void testElementPredicate() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("foo[a=0]");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		{
			Segment seg = xpe.segments.get(0);
			assertNotNull(seg);
			assertEquals("foo", seg.elementName);
			assertEquals(1, seg.predicates.size());
			for (Predicate p : seg.predicates)
			{
				assertNotNull(p);
			}
		}
	}
	
	@Test
	public void testSinglePredicate() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("[a=0]");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("", seg.elementName);
		assertEquals(1, seg.predicates.size());
		for (Predicate p : seg.predicates)
		{
			assertNotNull(p);
		}
	}
	
	@Test
	public void testTwoPredicates() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("[a=0][b=1]");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("", seg.elementName);
		assertEquals(2, seg.predicates.size());
		for (Predicate p : seg.predicates)
		{
			assertNotNull(p);
		}
	}
	
	@Test
	public void testTwoPredicatesSpace() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("[a=0] [b=1]");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("", seg.elementName);
		assertEquals(2, seg.predicates.size());
		for (Predicate p : seg.predicates)
		{
			assertNotNull(p);
		}
	}
	
	@Test
	public void testTextSegment1() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("text()");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertTrue(seg instanceof TextSegment);
	}
	
	@Test
	public void testTextSegment2() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.newXPath("foo/text()");
		assertNotNull(xpe);
		assertEquals(2, xpe.segments.size());
		Segment seg = xpe.segments.get(1);
		assertNotNull(seg);
		assertTrue(seg instanceof TextSegment);
	}

	@Test(expected = InvalidXPath.class)
	public void testNull() throws InvalidXPath {
		XPathExpression.newXPath(null);
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed1() throws InvalidXPath {
		XPathExpression.newXPath("[");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed2() throws InvalidXPath {
		XPathExpression.newXPath("a[");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed3() throws InvalidXPath {
		XPathExpression.newXPath("[]");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed4() throws InvalidXPath {
		XPathExpression.newXPath("a]");
		fail("Should throw an exception");
	}
	
	@Test(expected = InvalidXPath.class)
	public void testMalformed5() throws InvalidXPath {
		XPathExpression.newXPath("abc[a=1]][b=0]");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformed6() throws InvalidXPath {
		XPathExpression.newXPath("ab]cd[foo=0]");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformedEquality1() throws InvalidXPath {
		XPathExpression.newXPath("a[=]");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformedEquality2() throws InvalidXPath {
		XPathExpression.newXPath("a[=c]");
		fail("Should throw an exception");
	}

	@Test(expected = InvalidXPath.class)
	public void testMalformedEquality3() throws InvalidXPath {
		XPathExpression.newXPath("a[c=]");
		fail("Should throw an exception");
	}
	
}
