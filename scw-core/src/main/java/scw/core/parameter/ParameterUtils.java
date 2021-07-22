package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.lang.NestedExceptionUtils;

public final class ParameterUtils {
	private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new Jdk8ParameterNameDiscoverer();

	private ParameterUtils() {
	};

	public static ParameterNameDiscoverer getParameterNameDiscoverer() {
		return PARAMETER_NAME_DISCOVERER;
	}

	public static String[] getParameterNames(Executable executable) {
		return getParameterNames(PARAMETER_NAME_DISCOVERER, executable);
	}

	public static String[] getParameterNames(ParameterNameDiscoverer parameterNameDiscoverer, Executable executable) {
		if (executable instanceof Method) {
			return parameterNameDiscoverer.getParameterNames((Method) executable);
		} else if (executable instanceof Constructor) {
			return parameterNameDiscoverer.getParameterNames((Constructor<?>) executable);
		}
		throw NestedExceptionUtils.shouldNeverGetHere();
	}

	public static ParameterDescriptor[] getParameters(ParameterNameDiscoverer parameterNameDiscoverer,
			Executable executable) {
		String[] names = getParameterNames(parameterNameDiscoverer, executable);
		if (ArrayUtils.isEmpty(names)) {
			return ParameterDescriptor.EMPTY_ARRAY;
		}

		Annotation[][] parameterAnnoatations = executable.getParameterAnnotations();
		Type[] parameterGenericTypes = executable.getGenericParameterTypes();
		Class<?>[] parameterTypes = executable.getParameterTypes();
		ParameterDescriptor[] parameterDefinitions = new ParameterDescriptor[names.length];
		for (int i = 0; i < names.length; i++) {
			AnnotatedElement annotatedElement = MultiAnnotatedElement
					.forAnnotatedElements(AnnotatedElementUtils.forAnnotations(parameterAnnoatations[i]), executable);
			parameterDefinitions[i] = new DefaultParameterDescriptor(names[i], annotatedElement, parameterTypes[i],
					parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}

	public static ParameterDescriptor[] getParameters(Executable executable) {
		return getParameters(PARAMETER_NAME_DISCOVERER, executable);
	}

	public static LinkedHashMap<String, Object> getParameterMap(ParameterDescriptor[] parameterDescriptors,
			Object[] args) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(parameterDescriptors.length);
		for (int i = 0; i < parameterDescriptors.length; i++) {
			ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
			map.put(parameterDescriptor.getName(), args[i]);
		}
		return map;
	}

	public static LinkedHashMap<String, Object> getParameterMap(Method method, Object[] args) {
		String[] names = getParameterNames(method);
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(names.length);
		for (int i = 0; i < names.length; i++) {
			map.put(names[i], args[i]);
		}
		return map;
	}

	public static LinkedHashMap<String, Object> getParameterMap(Constructor<?> constructor, Object[] args) {
		String[] names = getParameterNames(constructor);
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(names.length);
		for (int i = 0; i < names.length; i++) {
			map.put(names[i], args[i]);
		}
		return map;
	}

	public static boolean isAssignableValue(ParameterDescriptors parameterDescriptors, Object[] params) {
		// 异或运算，如果两个不同则结果为1
		if (parameterDescriptors.size() == 0 ^ ArrayUtils.isEmpty(params)) {
			return false;
		}

		if (parameterDescriptors.size() != params.length) {
			return false;
		}

		int index = 0;
		for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
			Object value = params[index++];
			if (value == null && !parameterDescriptor.isNullable()) {
				return false;
			}

			if (!ClassUtils.isAssignableValue(parameterDescriptor.getType(), value)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isisAssignable(ParameterDescriptors parameterDescriptors, Class<?>[] types) {
		// 异或运算，如果两个不同则结果为1
		if (parameterDescriptors.size() == 0 ^ ArrayUtils.isEmpty(types)) {
			return false;
		}

		return ClassUtils.isAssignable(Arrays.asList(parameterDescriptors.getTypes()), Arrays.asList(types));
	}
}
