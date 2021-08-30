package io.basc.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.reflect.ReflectionUtils;

public class UnsafeUtils {
	private static final Object UNSAFE;
	private static final String CLASS_NAME = "sun.misc.Unsafe";
	private static final Method ALLOCATE_INSTANCE = getMethod(
			"allocateInstance", Class.class);

	static {
		UNSAFE = getInvokeInstance();
	}

	private static Object getInvokeInstance() {
		try {
			Class<?> clz = ClassUtils.forName(CLASS_NAME, null);
			Field f = clz.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return f.get(null);
		} catch (Exception e) {
			return null;
		}
	}

	private UnsafeUtils() {
	}

	public static boolean isSupport() {
		return UNSAFE != null;
	}

	public static Object getUnsafe() {
		return UNSAFE;
	}

	public static Method getMethod(String methodName,
			Class<?>... parameterTypes) {
		return ReflectionUtils
				.getMethod(CLASS_NAME, null, methodName, parameterTypes);
	}

	public static Object invoke(Method method, Object... args) {
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers())? null:UNSAFE, args);
	}
	
	public static Object allocateInstance(Class<?> type) {
		return invoke(ALLOCATE_INSTANCE, type);
	}
}
