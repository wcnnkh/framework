package run.soeasy.framework.core.type;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

public class LambdaTest {
	@Test
	public void test() {
		Runnable runnable = () -> System.out.println("unregister");
		A a = () -> System.out.println("a unregister");

		System.out.println("-----------------------------");
		for (Class<?> clazz : runnable.getClass().getInterfaces()) {
			System.out.println(clazz);
		}

		for (Method method : runnable.getClass().getDeclaredMethods()) {
			System.out.println(method);
		}

		System.out.println("-----------------------------");
		for (Class<?> clazz : a.getClass().getInterfaces()) {
			System.out.println(clazz);
		}

		for (Method method : a.getClass().getDeclaredMethods()) {
			System.out.println(method);
		}

		assertTrue(ClassUtils.isLambdaClass(runnable.getClass()));
		assertTrue(ClassUtils.isLambdaClass(a.getClass()));
	}

	private static interface A extends Runnable {
	}
}
