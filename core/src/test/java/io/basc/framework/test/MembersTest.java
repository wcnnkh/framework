package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import io.basc.framework.core.Members;
import io.basc.framework.core.reflect.ReflectionUtils;

public class MembersTest {

	@Test
	public void test() {
		Members<Field> members = ReflectionUtils.getDeclaredFields(B.class).all();
		assertTrue(members.getElements().count() == 7);
		System.out.println(ReflectionUtils.getDeclaredFields(B.class).all()
				.concat(ReflectionUtils.getFields(B.class).getElements()).all()
				.convert((es) -> es.map((e) -> e.getName())).all().getElements().count() == 7);
		Assert.assertTrue(ReflectionUtils.getDeclaredFields(B.class).all().filter((e) -> e.getName().equals("a")).all()
				.getElements().count() == 2);
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
