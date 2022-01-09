package io.basc.framework.test;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;

public class MembersTest {
	
	@Test
	public void test() {
		ReflectionUtils.getDeclaredFields(B.class).withAll().filter((e) -> e.getName().contentEquals("a")).streamAll().forEach((e) -> System.out.println(e));
	}
	
	public static class A {
		protected String a;
		protected int b;
	}
	
	public static class B extends A{
		public Object c;
		public boolean d;
	}
}
