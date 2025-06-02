package run.soeasy.framework.core.type;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

public class MembersTest {

	@Test
	public void test() {
		ClassMembersLoader<Field> members = ReflectionUtils.getDeclaredFields(B.class).withAll();
		assertTrue(members.count() == 5);
		Assert.assertTrue(members.filter((e) -> e.getName().equals("a")).count() == 2);

		for (ClassMembers<Field> classMembers : members.getElements()) {
			System.out.println(classMembers.getDeclaringClass() + "," + classMembers.count());
		}
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
