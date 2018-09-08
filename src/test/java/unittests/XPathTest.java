package unittests;

import org.junit.Test;
import simplexml.error.InvalidXPath;
import simplexml.model.Element;
import simplexml.xpath.XPathExpression;

import java.util.Collection;

import static org.junit.Assert.*;
import static simplexml.model.Element.element;

public class XPathTest {

	@Test
	public void testEmpty() throws InvalidXPath {
		Element doc = element("a");
		XPathExpression xpath = XPathExpression.toXPathExpression("");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(0, result.size());
	}

	@Test
	public void testSingleEmptyText() throws InvalidXPath {
		Element doc = element("a");
		XPathExpression xpath = XPathExpression.toXPathExpression("a/text()");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(0, result.size());
	}

	@Test
	public void testSingle() throws InvalidXPath {
		Element doc = element("a");
		XPathExpression xpath = XPathExpression.toXPathExpression("a");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
	}

	@Test
	public void testSingleValue() throws InvalidXPath {
		Element doc = element("a").text("1");
		XPathExpression xpath = XPathExpression.toXPathExpression("a");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
	}

	@Test
	public void testSingleValueText() throws InvalidXPath {
		Element doc = element("a").text("1");
		XPathExpression xpath = XPathExpression.toXPathExpression("a/text()");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
		for (Object xe : result) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testtwoLevels1() throws InvalidXPath {
		Element doc = element("root").child(element("a"));
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
	}

	@Test
	public void testtwoLevels2() throws InvalidXPath {
		Element doc = element("root").child(element("a").text("1"));
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
		for (Object xe : result) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testMultipleValues1() throws InvalidXPath {
		Element doc = element("root").child(element("a").text("1")).child(element("a").text("2"));
//				Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(2, result.size());
		for (Object xe : result) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testMultipleValues2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("b").text("123"))
				.child(element("a").text("2"));
			//Element.toXmlElement("<root><a>1</a><b>123</b><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(2, result.size());
		for (Object xe : result) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testPredicate1() throws InvalidXPath {
		Element doc = element("root")
			.child(element("foo")
				.child(element("bar")
					.text("0"))
				.child(element("baz")
					.text("0")))
			.child(element("foo")
				.child(element("bar")
					.text("1"))
				.child(element("baz")
					.text("0")));
//		Element doc = Element.toXmlElement("<root><foo><bar>0</bar><baz>0</baz></foo><foo><bar>1</bar><baz>0</baz></foo></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/foo[bar=0]");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
		for (Object xe : result) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testPredicate2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("foo")
						.child(element("bar")
								.text("0"))
						.child(element("baz")
								.text("0")))
				.child(element("foo")
						.child(element("bar")
								.text("1"))
						.child(element("baz")
								.text("0")));
//		Element doc = Element.toXmlElement("<root><foo><bar>0</bar><baz>0</baz></foo><foo><bar>1</bar><baz>0</baz></foo></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/foo[zzz=0]");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(0, result.size());
	}

	@Test
	public void testPredicate3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("foo")
						.child(element("bar")
								.text("0"))
						.child(element("baz")
								.text("0")))
				.child(element("foo")
						.child(element("bar")
								.text("1"))
						.child(element("baz")
								.text("0")));
//		Element doc = Element.toXmlElement("<root><foo><bar>0</bar><baz>0</baz></foo><foo><bar>1</bar><baz>0</baz></foo></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/foo[bar=0][baz=0]");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(1, result.size());
		for (Object xe : result) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testPredicate4() throws InvalidXPath {
		Element doc = element("root")
				.child(element("foo")
						.child(element("bar")
								.text("0"))
						.child(element("baz")
								.text("0")))
				.child(element("foo")
						.child(element("bar")
								.text("1"))
						.child(element("baz")
								.text("0")));
//		Element doc = Element.toXmlElement("<root><foo><bar>0</bar><baz>0</baz></foo><foo><bar>1</bar><baz>0</baz></foo></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/foo[bar=0][baz=6]");
		Collection<Object> result = xpath.evaluate(doc);
		assertEquals(0, result.size());
	}

	@Test
	public void testText1() throws InvalidXPath {
		Element doc = element("root")
			.child(element("a").text("1"))
			.child(element("a")
				.child(element("b")
						.text("2")));
//		Element doc = Element.toXmlElement("<root><a>1</a><a><b>2</b></a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Collection<Object> result = xpath.evaluate(doc);
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	public void testAnyString4() throws InvalidXPath {
		Element doc = element("root")
			.child(element("a")
				.child(element("b")
					.text("5")))
			.child(element("a")
				.child(element("b")
					.text("2")));
//		Element doc = Element.toXmlElement("<root><a><b>5</b></a><a><b>2</b></a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		String result = xpath.evaluateAnyString(doc);
		assertNotNull(result);
		assertEquals("", result);
	}

	@Test
	public void testAnyNumber4() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a")
						.child(element("b")
								.text("5")))
				.child(element("a")
						.child(element("b")
								.text("2")));
//		Element doc = Element.toXmlElement("<root><a><b>5</b></a><a><b>2</b></a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		Number result = xpath.evaluateAnyNumber(doc);
		assertNull(result);
	}

	@Test
	public void testAny1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Object result = xpath.evaluateAny(doc);
		assertNotNull(result);
		assertTrue(result instanceof String);
	}

	@Test
	public void testAny2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/b");
		Object result = xpath.evaluateAny(doc);
		assertNull(result);
	}

	@Test
	public void testAnyString1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		String result = xpath.evaluateAnyString(doc);
		assertNotNull(result);
	}

	@Test
	public void testAnyString2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/b/text()");
		String result = xpath.evaluateAnyString(doc);
		assertEquals("", result);
	}

	@Test
	public void testAnyString3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").child(element("c")));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a><c></c></a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		String result = xpath.evaluateAnyString(doc);
		assertEquals("", result);
	}

	@Test
	public void testAnyNumber1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Number result = xpath.evaluateAnyNumber(doc);
		assertTrue(result.intValue() == 1 || result.intValue() == 2);
	}

	@Test
	public void testAnyNumber2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/b");
		Number result = xpath.evaluateAnyNumber(doc);
		assertNull(result);
	}

	@Test
	public void testAnyNumber3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("foo"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>foo</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Number result = xpath.evaluateAnyNumber(doc);
		assertNull(result);
	}

	@Test
	public void testAnyInt1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		int result = xpath.evaluateAnyInt(doc);
		assertTrue(result == 1 || result == 2);
	}

	@Test
	public void testAnyInt2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("xyz"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>xyz</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		int result = xpath.evaluateAnyInt(doc);
		assertEquals(1, result);
	}

	@Test
	public void testAnyInt3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("xyz"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>xyz</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		int result = xpath.evaluateAnyInt(doc);
		assertEquals(0, result);
	}

	@Test
	public void testAnyFloat1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		float result = xpath.evaluateAnyFloat(doc);
		assertTrue(result == 1 || result == 2);
	}

	@Test
	public void testAnyFloat2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("xyz"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>xyz</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		float result = xpath.evaluateAnyFloat(doc);
		assertTrue(Math.abs(1 - result) < 0.0001);
	}

	@Test
	public void testAnyFloat3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("xyz"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>xyz</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		float result = xpath.evaluateAnyFloat(doc);
		assertTrue(result < 0.0001);
	}

	@Test
	public void testNumbers1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Collection<Number> result = xpath.evaluateAsNumbers(doc);
		assertEquals(2, result.size());
	}

	@Test
	public void testNumbers2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("foo"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>foo</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Collection<Number> result = xpath.evaluateAsNumbers(doc);
		assertEquals(0, result.size());
	}

	@Test
	public void testNumbers3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("foo"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>foo</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		Collection<Number> result = xpath.evaluateAsNumbers(doc);
		assertEquals(0, result.size());
	}

	@Test
	public void testStrings1() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").text("1"))
				.child(element("a").text("2"));
//		Element doc = Element.toXmlElement("<root><a>1</a><a>2</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Collection<String> result = xpath.evaluateAsStrings(doc);
		assertEquals(2, result.size());
	}

	@Test
	public void testStrings2() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("foo"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>foo</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a/text()");
		Collection<String> result = xpath.evaluateAsStrings(doc);
		assertEquals(1, result.size());
	}

	@Test
	public void testStrings3() throws InvalidXPath {
		Element doc = element("root")
				.child(element("a").child(element("b")))
				.child(element("a").text("foo"));
//		Element doc = Element.toXmlElement("<root><a><b></b></a><a>foo</a></root>");
		XPathExpression xpath = XPathExpression.toXPathExpression("root/a");
		Collection<String> result = xpath.evaluateAsStrings(doc);
		assertEquals(0, result.size());
	}

}
