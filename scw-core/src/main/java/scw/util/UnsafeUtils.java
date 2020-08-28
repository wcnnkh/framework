package scw.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;

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

	public static Method getMethod(String methodName,
			Class<?>... parameterTypes) {
		return ReflectionUtils
				.getMethod(CLASS_NAME, methodName, parameterTypes);
	}

	public static Object invoke(Method method, Object... args) {
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers())? null:UNSAFE, args);
	}
	
	public static Object allocateInstance(Class<?> type) {
		return invoke(ALLOCATE_INSTANCE, type);
	}
}
