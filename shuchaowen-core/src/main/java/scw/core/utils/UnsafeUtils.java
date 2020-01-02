package scw.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UnsafeUtils {
	private static final Object UNSAFE;
	private static final String CLASS_NAME = "sun.misc.Unsafe";
	private static final Method ALLOCATE_INSTANCE = getMethod("allocateInstance", Class.class);

	static {
		UNSAFE = getInvokeInstance();
	}

	private static Object getInvokeInstance() {
		try {
			Class<?> clz = ClassUtils.forName(CLASS_NAME);
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

	public static Method getMethod(String methodName, Class<?>... parameterTypes) {
		try {
			return ClassUtils.forName(CLASS_NAME).getMethod(methodName, parameterTypes);
		} catch (ClassNotFoundException e) {
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}
		return null;
	}

	public static Object invoke(Method method, Object... args) {
		try {
			return method.invoke(method, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(method.toString(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(method.toString(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(method.toString(), e);
		}
	}

	public static Object allocateInstance(Class<?> type) {
		return invoke(ALLOCATE_INSTANCE, type);
	}
}
