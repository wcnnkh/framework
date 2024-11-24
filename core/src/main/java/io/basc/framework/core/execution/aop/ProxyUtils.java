package io.basc.framework.core.execution.aop;

import java.lang.reflect.Method;

import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.reflect.ReflectionUtils;

public class ProxyUtils {
	public static boolean isIgnoreMethod(Method method) {
		return ReflectionUtils.isHashCodeMethod(method) && ReflectionUtils.isToStringMethod(method)
				&& ReflectionUtils.isEqualsMethod(method);
	}

	public static int invokeHashCode(Object instance, Method method) {
		return System.identityHashCode(instance);
	}

	public static String invokeToString(Object instance, Method method) {
		return instance.getClass().getName() + "@" + Integer.toHexString(invokeHashCode(instance, method));
	}

	public static boolean invokeEquals(Object instance, Method method, Object[] args) {
		Object value = ArrayUtils.isEmpty(args) ? null : args[0];
		if (value == null) {
			return false;
		}
		return value.equals(instance);
	}

	public static Object invokeIgnoreMethod(Object instance, Method method, Object[] args) {
		if (ReflectionUtils.isHashCodeMethod(method)) {
			return invokeHashCode(instance, method);
		}

		if (ReflectionUtils.isToStringMethod(method)) {
			return invokeToString(instance, method);
		}

		if (ReflectionUtils.isEqualsMethod(method)) {
			return invokeEquals(instance, method, args);
		}

		throw new UnsupportedOperationException(method.toString());
	}
}
