package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import io.basc.framework.util.StringUtils;

public class StringTest {
	@Test
	public void indexOf() {
		String text = "abcdeft";
		String index = "de";
		assertTrue(text.indexOf(index) == StringUtils.indexOf(text, index));
		assertTrue(text.lastIndexOf(index) == StringUtils.lastIndexOf(text, index));
		assertTrue(text.indexOf(index, 2) == StringUtils.indexOf(text, index, 2));
		assertTrue(text.lastIndexOf(index, 1) == StringUtils.lastIndexOf(text, index, 1));
	}

	@Test
	public void split() {
		String text = "a,b, c;d e";
		assertTrue(Arrays.equals(text.split(","), StringUtils.splitToArray(text, false, false, ",")));
	}
}
