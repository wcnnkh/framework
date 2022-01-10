package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;

public class MembersTest {

	@Test
	public void test() {
		assertTrue(ReflectionUtils.getDeclaredFields(B.class).withAll().filter((e) -> e.getName().equals("a"))
				.streamAll().count() == 2);
	}

	public static class A {
		protected String a;
		protected int b;
	}

	public static class B extends A {
		protected int[] a;
		public Object c;
		public boolean d;
	}
}
