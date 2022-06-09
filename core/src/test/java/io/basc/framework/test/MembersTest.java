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
		Members<Field> members = ReflectionUtils.getDeclaredFields(B.class).withAll().all().all().all().all()
				.withMethod(Members.DIRECT).withAll().withMethod(Members.REFUSE).all();
		assertTrue(members.stream().count() == 7);
		assertTrue(ReflectionUtils.getDeclaredFields(B.class).withAll().withInterfaces().withSuperclass()
				.withStream(() -> null).all().map((e) -> e.getName()).streamAll().count() == 5);
		Assert.assertTrue(ReflectionUtils.getDeclaredFields(B.class).withAll().withAll()
				.filter((e) -> e.getName().equals("a")).streamAll().count() == 2);
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
