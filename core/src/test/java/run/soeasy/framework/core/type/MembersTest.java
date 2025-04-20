package run.soeasy.framework.core.type;

import java.lang.reflect.Method;

import org.junit.Test;

public class MembersTest {
	private static interface A {
		public void a();
	}

	public static interface F extends A {
		public void f();
	}

	private static class B implements A {
		public void b() {

		}

		@Override
		public void a() {
			// TODO Auto-generated method stub

		}
	}

	private static class C extends B {
		public void c() {
		}
	}

	private static class D extends C {
		public void d() {

		}
	}

	@Test
	public void test() {
		ClassMembersLoader<Method> loader = new ClassMembersLoader<>(B.class, Class::getDeclaredMethods);
		loader = loader.withInterfaces();
		for (Method member : loader) {
			System.out.println(member);
		}
	}
}
