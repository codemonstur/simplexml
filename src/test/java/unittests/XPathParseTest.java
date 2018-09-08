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
		XPathExpression xpe = XPathExpression.toXPathExpression("");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("", seg.elementName);
	}

	@Test
	public void testSingleElement() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.toXPathExpression("a");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertEquals("a", seg.elementName);
	}
	
	@Test
	public void testTwoElements() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.toXPathExpression("foo/bar");
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
		XPathExpression xpe = XPathExpression.toXPathExpression("foo[a=0]");
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
		XPathExpression xpe = XPathExpression.toXPathExpression("[a=0]");
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
		XPathExpression xpe = XPathExpression.toXPathExpression("[a=0][b=1]");
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
		XPathExpression xpe = XPathExpression.toXPathExpression("[a=0] [b=1]");
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
		XPathExpression xpe = XPathExpression.toXPathExpression("text()");
		assertNotNull(xpe);
		assertEquals(1, xpe.segments.size());
		Segment seg = xpe.segments.get(0);
		assertNotNull(seg);
		assertTrue(seg instanceof TextSegment);
	}
	
	@Test
	public void testTextSegment2() throws InvalidXPath {
		XPathExpression xpe = XPathExpression.toXPathExpression("foo/text()");
		assertNotNull(xpe);
		assertEquals(2, xpe.segments.size());
		Segment seg = xpe.segments.get(1);
		assertNotNull(seg);
		assertTrue(seg instanceof TextSegment);
	}
	
	@Test
	public void testNull() {
		try {
			XPathExpression.toXPathExpression(null);
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");
	}
	
	@Test
	public void testMalformed1() {
		try {
			XPathExpression.toXPathExpression("[");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformed2() {
		try {
			XPathExpression.toXPathExpression("a[");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformed3() {
		try {
			XPathExpression.toXPathExpression("[]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformed4() {
		try {
			XPathExpression.toXPathExpression("a]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformed5() {
		try {
			XPathExpression.toXPathExpression("abc[a=1]][b=0]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformed6() {
		try {
			XPathExpression.toXPathExpression("ab]cd[foo=0]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformedEquality1() {
		try {
			XPathExpression.toXPathExpression("a[=]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformedEquality2() {
		try {
			XPathExpression.toXPathExpression("a[=c]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
	@Test
	public void testMalformedEquality3() {
		try {
			XPathExpression.toXPathExpression("a[c=]");
		}
		catch (InvalidXPath ex) {
			return;
		}
		fail("Should throw an exception");		
	}
	
}
