package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.junit.Test;

import io.basc.framework.register.Registration;
import io.basc.framework.util.ClassUtils;

public class LambdaTest {
	@Test
	public void test() {
		Registration registration = () -> System.out.println("unregister");
		A a = () -> System.out.println("a unregister");

		System.out.println("-----------------------------");
		for (Class<?> clazz : registration.getClass().getInterfaces()) {
			System.out.println(clazz);
		}

		for (Method method : registration.getClass().getDeclaredMethods()) {
			System.out.println(method);
		}

		System.out.println("-----------------------------");
		for (Class<?> clazz : a.getClass().getInterfaces()) {
			System.out.println(clazz);
		}

		for (Method method : a.getClass().getDeclaredMethods()) {
			System.out.println(method);
		}

		assertTrue(ClassUtils.isLambdaClass(registration.getClass()));
		assertTrue(ClassUtils.isLambdaClass(a.getClass()));
	}

	private static interface A extends Registration {

		@Override
		default boolean isInvalid() {
			return false;
		}

		@Override
		default Registration and(Registration registration) {
			return Registration.super.and(registration);
		}
	}
}
