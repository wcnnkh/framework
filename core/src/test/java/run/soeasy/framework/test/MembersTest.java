package run.soeasy.framework.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import run.soeasy.framework.util.reflect.ReflectionUtils;
import run.soeasy.framework.util.type.Members;

public class MembersTest {

	@Test
	public void test() {
		Members<Field> members = ReflectionUtils.getDeclaredFields(B.class).all();
		assertTrue(members.getElements().count().longValue() == 5);
		Assert.assertTrue(members.filter((e) -> e.getName().equals("a")).getElements().count().longValue() == 2);
		Members<Field> concatFields = ReflectionUtils.getDeclaredFields(B.class).all()
				.concat(ReflectionUtils.getFields(B.class).getElements());
		assertTrue(concatFields.getElements().count().longValue() == 7);
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
