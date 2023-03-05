package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.lang.NamedInheritableThreadLocal;

public class ThreadLocalTest {
	private static InheritableThreadLocal<Object> inheritableThreadLocal = new NamedInheritableThreadLocal<>("test",
			true);

	@Test
	public void inheritableThreadLocalTest() {
		Object v = "abc";
		inheritableThreadLocal.set(v);

		for (int i = 0; i < 2; i++) {
			final int a = i;
			startThread(() -> {
				System.out.println(a + ":" + inheritableThreadLocal.get());
				assertTrue(v.equals(inheritableThreadLocal.get()));
				for (int x = 0; x < 1; x++) {
					final int b = x;
					startThread(() -> {
						System.out.println(a + ":" + b + ":" + inheritableThreadLocal.get());
						assertTrue(v.equals(inheritableThreadLocal.get()));
					});
				}
			});
		}
	}

	public static void startThread(Runnable runnable) {
		new Thread(runnable).start();
	}
}