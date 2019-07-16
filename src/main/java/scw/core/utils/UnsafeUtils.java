package scw.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumMap;

import scw.core.exception.NotSupportException;

public class UnsafeUtils {
	private static final Object INVOKE_INSTANCE;
	private static final EnumMap<MethodType, Method> METHOD_MAP = new EnumMap<MethodType, Method>(MethodType.class);
	private static final String CLASS_NAME = "sun.misc.Unsafe";

	static {
		INVOKE_INSTANCE = getInvokeInstance();
		if (INVOKE_INSTANCE != null) {
			try {
				init(Class.forName(CLASS_NAME));
			} catch (Exception e) {
			}
		}
	}

	private static Object getInvokeInstance() {
		try {
			Class<?> clz = Class.forName(CLASS_NAME);
			Field f = clz.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return f.get(null);
		} catch (Exception e) {
			return null;
		}
	}

	private static void init(Class<?> clz) throws Exception {
		METHOD_MAP.put(MethodType.allocateInstance, clz.getMethod("allocateInstance", Class.class));
	}

	private UnsafeUtils() {
	}

	public static boolean isSupport() {
		return INVOKE_INSTANCE != null;
	}

	private enum MethodType {
		allocateInstance,
		;

		public Object invoke(Object... args) {
			try {
				return METHOD_MAP.get(this).invoke(INVOKE_INSTANCE, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static Object getUnsafe() {
		return INVOKE_INSTANCE;
	}

	public static Method getMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
		try {
			return Class.forName(CLASS_NAME).getMethod(methodName, parameterTypes);
		} catch (ClassNotFoundException e) {
			throw new NotSupportException(CLASS_NAME);
		}
	}

	public static Object allocateInstance(Class<?> type) {
		return MethodType.allocateInstance.invoke(type);
	}
}
