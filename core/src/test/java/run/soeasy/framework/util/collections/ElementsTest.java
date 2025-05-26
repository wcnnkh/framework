package run.soeasy.framework.util.collections;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.core.collection.Elements;

public class ElementsTest {
	@Test
	public void concat() {
		Elements<String> left = Elements.forArray("a", "b", "c");
		Elements<String> right = Elements.forArray("d", "e", "f");

		Elements<String> all = left.concat(right);

		assertTrue(all.count() == (left.count() + right.count()));
	}
}
