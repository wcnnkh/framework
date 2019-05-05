package scw.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class AnnotationUtils {
	private AnnotationUtils() {
	};

	public static Method[] getAnnoationMethods(Class<?> type, boolean useSuper, boolean useInterface,
			Class<? extends Annotation> annotationClass) {
		Map<String, Method> map = new HashMap<String, Method>();
		Class<?> clz = type;
		while (clz != null) {
			appendAnnoationMethod(map, clz, annotationClass);
			if (useInterface) {
				appendAnnoationInterfaceMethod(map, clz, annotationClass);
			}

			if (!useSuper) {
				break;
			}

			clz = clz.getSuperclass();
		}
		return map.values().toArray(new Method[map.size()]);
	}

	private static void appendAnnoationInterfaceMethod(Map<String, Method> methodMap, Class<?> type,
			Class<? extends Annotation> annotationClass) {
		Class<?>[] interfaces = type.getInterfaces();
		if (interfaces == null || interfaces.length == 0) {
			return;
		}

		for (Class<?> clz : interfaces) {
			appendAnnoationMethod(methodMap, clz, annotationClass);
			appendAnnoationInterfaceMethod(methodMap, clz, annotationClass);
		}
	}

	private static void appendAnnoationMethod(Map<String, Method> methodMap, Class<?> type,
			Class<? extends Annotation> annotationClass) {
		for (Method method : type.getDeclaredMethods()) {
			Annotation annotation = method.getAnnotation(annotationClass);
			if (annotation == null) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(method.getName());
			for (Class<?> t : method.getParameterTypes()) {
				sb.append("&");
				sb.append(t.getName());
			}

			String key = sb.toString();
			if (methodMap.containsKey(key)) {
				continue;
			}

			methodMap.put(key, method);
		}
	}
}