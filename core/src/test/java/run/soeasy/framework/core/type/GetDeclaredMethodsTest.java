package run.soeasy.framework.core.type;

import org.junit.Test;

import run.soeasy.framework.core.ObjectUtils;

public class GetDeclaredMethodsTest {
	@Test
	public void test() {
		System.out.println(ObjectUtils.toString(A.class.getDeclaredMethods()));
		System.out.println(ObjectUtils.toString(B.class.getSuperclass()));
		System.out.println(ObjectUtils.toString(B.class.getInterfaces()));
		System.out.println(ObjectUtils.toString(E.class.getDeclaredMethods()));
		System.out.println("-------");
		ReflectionUtils.getMethods(E.class).withAll().forEach((e) -> System.out.println(e));
	}

	static interface A {
		void a();

		default void aa() {
		}
	}

	static interface B extends A {
		void b();

		default void bb() {
		}
	}

	static interface C {
		void c();

		default void cc() {
		};

	}

	static class D implements C {

		@Override
		public void c() {
		}

	}

	static class E implements B {

		@Override
		public void a() {

		}

		@Override
		public void b() {
		}

	}
}
