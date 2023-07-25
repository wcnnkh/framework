package io.basc.framework.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.match.AntPathMatcher;

public class AntPathMatcherTest {
	@Test
	public void test() {
		AntPathMatcher matcher = new AntPathMatcher(".");
		assertTrue(matcher.match("a.b.*", "a.b.c"));
		assertFalse(matcher.match("a.b.*", "a.b.c.d"));
		assertTrue(matcher.match("a.b.**", "a.b.c.d"));
	}
}
