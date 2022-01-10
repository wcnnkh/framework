package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.core.reflect.ReflectionUtils;

public class ReflectionApiTest {
	@Test
	public void test() throws Exception {
		A a = ReflectionApi.allocateInstance(A.class);
		assertTrue(a.a == 0);
		A a1 = ReflectionApi.getConstructorForSerialization(A.class).newInstance();
		assertTrue(a1.a == 0);
		A a2 = ReflectionUtils.getConstructor(A.class).newInstance();
		assertTrue(a2.a == 1);

		B b = ReflectionApi.getConstructor(B.class).newInstance();
		assertTrue(b.b == 0);

		B b1 = ReflectionApi.allocateInstance(B.class);
		assertTrue(b1.b == 0);

		A aa = ReflectionApi.newInstance(A.class);
		assertTrue(aa.a == 1);
		B bb = ReflectionApi.newInstance(B.class);
		assertTrue(bb.b == 0);

		ReflectionApi.newInstance(E.class);
	}

	private static enum E {
	}

	public static class A {
		public int a;

		private A() {
			a = 1;
		}
	}

	public static class B {
		public int b;

		private B(int b) {
			this.b = b;
		}
	}
}
