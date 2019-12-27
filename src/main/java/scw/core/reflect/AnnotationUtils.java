package scw.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.core.annotation.DELETE;
import scw.core.annotation.GET;
import scw.core.annotation.POST;
import scw.core.annotation.PUT;
import scw.core.utils.ArrayUtils;
import scw.lang.Description;
import scw.lang.Ignore;
import scw.lang.Nullable;
import scw.util.KeyValuePair;
import scw.util.SimpleKeyValuePair;

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
		for (Method method : ReflectionUtils.getDeclaredMethods(type)) {
			if (isDeprecated(method)) {
				continue;
			}

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

	public static boolean isDeprecated(AccessibleObject accessibleObject) {
		return accessibleObject.getAnnotation(Deprecated.class) != null;
	}

	public static boolean isDeprecated(AnnotationFactory annotationFactory) {
		return annotationFactory.getAnnotation(Deprecated.class) != null;
	}

	public static LinkedList<Field> getAnnotationFieldList(Class<?> clazz, boolean isDeclared, boolean sup,
			Class<? extends Annotation> annotationClass) {
		Class<?> clz = clazz;
		LinkedList<Field> fieldList = new LinkedList<Field>();
		while (clz != null && clz != Object.class) {
			for (Field field : ReflectionUtils.getFields(clz, isDeclared)) {
				if (isDeprecated(field)) {
					continue;
				}

				Annotation annotation = field.getAnnotation(annotationClass);
				if (annotation == null) {
					continue;
				}

				fieldList.add(field);
				if (sup) {
					clz = clz.getSuperclass();
				} else {
					break;
				}
			}
		}
		return fieldList;
	}

	public static IdentityHashMap<Class<? extends Annotation>, Annotation> getAnnoataionMap(
			AnnotatedElement annotatedElement) {
		Annotation[] annotations = annotatedElement.getAnnotations();
		if (ArrayUtils.isEmpty(annotations)) {
			return null;
		}

		IdentityHashMap<Class<? extends Annotation>, Annotation> map = new IdentityHashMap<Class<? extends Annotation>, Annotation>(
				annotations.length);
		for (Annotation annotation : annotations) {
			map.put(annotation.getClass(), annotation);
		}
		return map;
	}

	public static <T extends Annotation> T getAnnotation(Class<T> type, AnnotatedElement annotatedElement,
			AnnotationFactory annotationFactory) {
		T t = annotatedElement.getAnnotation(type);
		T t2 = annotationFactory.getAnnotation(type);
		if (t2 != null) {
			t = t2;
		}
		return t;
	}

	public static <T extends Annotation> T getAnnotation(Class<T> type, AnnotationFactory annotationFactory,
			AnnotatedElement annotatedElement) {
		T t = annotationFactory.getAnnotation(type);
		T t2 = annotatedElement.getAnnotation(type);
		if (t2 != null) {
			t = t2;
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> type) {
		if (annotations == null || annotations.length == 0) {
			return null;
		}

		for (Annotation annotation : annotations) {
			if (annotation == null) {
				continue;
			}

			if (type.isInstance(annotation)) {
				return (T) annotation;
			}
		}

		return null;
	}

	public static <T extends Annotation> T getAnnotation(Annotation[][] annotations, Class<T> type, int index) {
		if (annotations == null || annotations.length == 0) {
			return null;
		}

		if (index >= annotations.length || index < 0) {
			return null;
		}

		return getAnnotation(annotations[index], type);
	}

	public static KeyValuePair<scw.net.http.Method, String> getHttpMethodAnnotation(AnnotatedElement annotatedElement) {
		GET get = annotatedElement.getAnnotation(GET.class);
		if (get != null) {
			return new SimpleKeyValuePair<scw.net.http.Method, String>(scw.net.http.Method.GET, get.value());
		}

		POST post = annotatedElement.getAnnotation(POST.class);
		if (post != null) {
			return new SimpleKeyValuePair<scw.net.http.Method, String>(scw.net.http.Method.POST, post.value());
		}

		DELETE delete = annotatedElement.getAnnotation(DELETE.class);
		if (delete != null) {
			return new SimpleKeyValuePair<scw.net.http.Method, String>(scw.net.http.Method.DELETE, delete.value());
		}

		PUT put = annotatedElement.getAnnotation(PUT.class);
		if (put != null) {
			return new SimpleKeyValuePair<scw.net.http.Method, String>(scw.net.http.Method.PUT, put.value());
		}

		return null;
	}

	/**
	 * 获取一个注解，后面覆盖前面
	 * 
	 * @param type
	 * @param annotatedElements
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation(Class<T> type, AnnotatedElement... annotatedElements) {
		T old = null;
		for (AnnotatedElement annotatedElement : annotatedElements) {
			T a = annotatedElement.getAnnotation(type);
			if (a != null) {
				old = a;
			}
		}
		return old;
	}

	/**
	 * 获取一个注解，后面覆盖前面
	 * 
	 * @param type
	 * @param annotationFactories
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation(Class<T> type, AnnotationFactory... annotationFactories) {
		T old = null;
		for (AnnotationFactory annotationFactory : annotationFactories) {
			T a = annotationFactory.getAnnotation(type);
			if (a != null) {
				old = a;
			}
		}
		return old;
	}

	public static boolean isIgnore(AnnotatedElement... annotatedElements) {
		Ignore ignore = getAnnotation(Ignore.class, annotatedElements);
		if (ignore == null) {
			return false;
		}

		return ignore.value();
	}

	/**
	 * 是否可以为空
	 * 
	 * @param annotationFactory
	 * @param defaultValue
	 * @return
	 */
	public static boolean isNullable(AnnotationFactory annotationFactory, boolean defaultValue) {
		Nullable nullable = annotationFactory.getAnnotation(Nullable.class);
		return nullable == null ? defaultValue : nullable.value();
	}

	public static String getDescription(AnnotationFactory annotationFactory, String defaultValue) {
		Description description = annotationFactory.getAnnotation(Description.class);
		return description == null ? defaultValue : description.value();
	}
}