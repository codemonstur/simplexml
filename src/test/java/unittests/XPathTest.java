package unittests;

import org.junit.Test;
import xmlparser.model.XmlElement;
import xmlparser.model.XmlElement.XmlTextElement;
import xmlparser.xpath.XPathExpression;

import java.util.Collection;

import static org.junit.Assert.*;
import static xmlparser.model.XmlElement.newElement;

public class XPathTest {

	@Test
	public void testEmpty() {
		final var doc = newElement("a").build();
		final var xpath = XPathExpression.newXPath("");

		final var actual = xpath.evaluate(doc);

		assertEquals(0, actual.size());
	}

	@Test
	public void testSingleEmptyText() {
		final var doc = newElement("a").build();
		final var xpath = XPathExpression.newXPath("a/text()");

		final var actual = xpath.evaluate(doc);

		assertEquals(0, actual.size());
	}

	@Test
	public void testSingle() {
		final var doc = newElement("a").build();
		final var xpath = XPathExpression.newXPath("a");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
	}

	@Test
	public void testSingleValue() {
		final var doc = newElement("a").text("1").build();
		final var xpath = XPathExpression.newXPath("a");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
	}

	@Test
	public void testSingleValueText() {
		final var doc = newElement("a").text("1").build();
		final var xpath = XPathExpression.newXPath("a/text()");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
		for (final var xe : actual) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testtwoLevels1() {
		final var doc = newElement("root").child(newElement("a")).build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
	}

	@Test
	public void testtwoLevels2() {
		final var doc = newElement("root").child(newElement("a").text("1")).build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
		for (final var xe : actual) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testMultipleValues1() {
		final var doc = newElement("root").child(newElement("a").text("1")).child(newElement("a").text("2")).build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluate(doc);

		assertEquals(2, actual.size());
		for (final var xe : actual) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testMultipleValues2() {
		final var doc = newElement("root")
				.child(newElement("a").text("1"))
				.child(newElement("b").text("123"))
				.child(newElement("a").text("2"))
				.build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluate(doc);

		assertEquals(2, actual.size());
		for (final var xe : actual) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testPredicate1() {
		final var doc = newElement("root")
			.child(newElement("foo")
				.child(newElement("bar")
					.text("0"))
				.child(newElement("baz")
					.text("0")))
			.child(newElement("foo")
				.child(newElement("bar")
					.text("1"))
				.child(newElement("baz")
					.text("0")))
			.build();
		final var xpath = XPathExpression.newXPath("root/foo[bar=0]");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
		for (final var xe : actual) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testPredicate2() {
		final var doc = newElement("root")
			.child(newElement("foo")
				.child(newElement("bar")
					.text("0"))
				.child(newElement("baz")
					.text("0")))
			.child(newElement("foo")
				.child(newElement("bar")
					.text("1"))
				.child(newElement("baz")
					.text("0")))
			.build();
		final var xpath = XPathExpression.newXPath("root/foo[zzz=0]");

		final var actual = xpath.evaluate(doc);

		assertEquals(0, actual.size());
	}

	@Test
	public void testPredicate3() {
		final var doc = newElement("root")
			.child(newElement("foo")
				.child(newElement("bar")
					.text("0"))
				.child(newElement("baz")
					.text("0")))
			.child(newElement("foo")
				.child(newElement("bar")
					.text("1"))
				.child(newElement("baz")
					.text("0")))
			.build();
		final var xpath = XPathExpression.newXPath("root/foo[bar=0][baz=0]");

		final var actual = xpath.evaluate(doc);

		assertEquals(1, actual.size());
		for (final var xe : actual) {
			assertNotNull(xe);
		}
	}

	@Test
	public void testPredicate4() {
		final var doc = newElement("root")
			.child(newElement("foo")
				.child(newElement("bar")
					.text("0"))
				.child(newElement("baz")
					.text("0")))
			.child(newElement("foo")
				.child(newElement("bar")
					.text("1"))
				.child(newElement("baz")
					.text("0")))
			.build();
		final var xpath = XPathExpression.newXPath("root/foo[bar=0][baz=6]");

		final var actual = xpath.evaluate(doc);

		assertEquals(0, actual.size());
	}

	@Test
	public void testText1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a")
				.child(newElement("b")
						.text("2")))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluate(doc);

		assertNotNull(actual);
		assertEquals(1, actual.size());
	}

	@Test
	public void testAnyString4() {
		final var doc = newElement("root")
			.child(newElement("a")
				.child(newElement("b")
					.text("5")))
			.child(newElement("a")
				.child(newElement("b")
					.text("2")))
			.build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluateAnyString(doc);

		assertNotNull(actual);
		assertEquals("", actual);
	}

	@Test
	public void testAnyNumber4() {
		final var doc = newElement("root")
			.child(newElement("a")
				.child(newElement("b")
					.text("5")))
			.child(newElement("a")
				.child(newElement("b")
					.text("2")))
			.build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluateAnyNumber(doc);

		assertNull(actual);
	}

	@Test
	public void testAny1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAny(doc);

		assertNotNull(actual);
		assertTrue(actual instanceof XmlTextElement);
	}

	@Test
	public void testAny2() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/b");

		final var actual = xpath.evaluateAny(doc);

		assertNull(actual);
	}

	@Test
	public void testAnyString1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAnyString(doc);

		assertNotNull(actual);
	}

	@Test
	public void testAnyString2() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/b/text()");

		final var actual = xpath.evaluateAnyString(doc);

		assertEquals("", actual);
	}

	@Test
	public void testAnyString3() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").child(newElement("c")))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var result = xpath.evaluateAnyString(doc);

		assertEquals("", result);
	}

	@Test
	public void testAnyNumber1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAnyNumber(doc);

		assertTrue(actual.intValue() == 1 || actual.intValue() == 2);
	}

	@Test
	public void testAnyNumber2() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/b");

		final var actual = xpath.evaluateAnyNumber(doc);

		assertNull(actual);
	}

	@Test
	public void testAnyNumber3() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("foo"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAnyNumber(doc);

		assertNull(actual);
	}

	@Test
	public void testAnyInt1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final int actual = xpath.evaluateAnyInt(doc);

		assertTrue(actual == 1 || actual == 2);
	}

	@Test
	public void testAnyInt2() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("xyz"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final int actual = xpath.evaluateAnyInt(doc);

		assertEquals(1, actual);
	}

	@Test
	public void testAnyInt3() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("xyz"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final int actual = xpath.evaluateAnyInt(doc);

		assertEquals(0, actual);
	}

	@Test
	public void testAnyFloat1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAnyFloat(doc);

		assertTrue(actual == 1 || actual == 2);
	}

	@Test
	public void testAnyFloat2() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("xyz"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAnyFloat(doc);

		assertTrue(Math.abs(1 - actual) < 0.0001);
	}

	@Test
	public void testAnyFloat3() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("xyz"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAnyFloat(doc);

		assertTrue(actual < 0.0001);
	}

	@Test
	public void testNumbers1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAsNumbers(doc);

		assertEquals(2, actual.size());
	}

	@Test
	public void testNumbers2() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("foo"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAsNumbers(doc);

		assertEquals(0, actual.size());
	}

	@Test
	public void testNumbers3() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("foo"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluateAsNumbers(doc);

		assertEquals(0, actual.size());
	}

	@Test
	public void testStrings1() {
		final var doc = newElement("root")
			.child(newElement("a").text("1"))
			.child(newElement("a").text("2"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAsStrings(doc);

		assertEquals(2, actual.size());
	}

	@Test
	public void testStrings2() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("foo"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a/text()");

		final var actual = xpath.evaluateAsStrings(doc);

		assertEquals(1, actual.size());
	}

	@Test
	public void testStrings3() {
		final var doc = newElement("root")
			.child(newElement("a").child(newElement("b")))
			.child(newElement("a").text("foo"))
			.build();
		final var xpath = XPathExpression.newXPath("root/a");

		final var actual = xpath.evaluateAsStrings(doc);

		assertEquals(0, actual.size());
	}

}
