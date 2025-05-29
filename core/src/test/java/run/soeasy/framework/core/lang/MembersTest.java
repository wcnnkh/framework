package run.soeasy.framework.core.lang;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.core.ClassMembersLoader;
import run.soeasy.framework.core.ReflectionUtils;
import run.soeasy.framework.core.collection.Elements;

public class MembersTest {

	@Test
	public void test() {
		ClassMembersLoader<Field> members = ReflectionUtils.getDeclaredFields(B.class).withAll();
		assertTrue(members.getElements().count() == 5);
		Assert.assertTrue(members.filter((e) -> e.getName().equals("a")).count() == 2);
		Elements<Field> concatFields = ReflectionUtils.getDeclaredFields(B.class).withAll()
				.concat(ReflectionUtils.getFields(B.class));
		assertTrue(concatFields.count() == 7);
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
