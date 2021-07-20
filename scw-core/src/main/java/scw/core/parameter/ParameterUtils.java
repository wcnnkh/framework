package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.MultiAnnotatedElement;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.util.JavaVersion;

public final class ParameterUtils {
	private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER;

	static {
		ParameterNameDiscoverer parameterNameDiscoverer = null;
		if (JavaVersion.INSTANCE.getMasterVersion() >= 8) {
			parameterNameDiscoverer = ClassUtils.newInstance("scw.core.parameter.Jdk8ParameterNameDiscoverer", null);
		} else {
			parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		}

		PARAMETER_NAME_DISCOVERER = parameterNameDiscoverer == null ? new LocalVariableTableParameterNameDiscoverer()
				: parameterNameDiscoverer;
	}

	private ParameterUtils() {
	};

	public static ParameterNameDiscoverer getParameterNameDiscoverer() {
		return PARAMETER_NAME_DISCOVERER;
	}

	public static String[] getParameterNames(Method method) {
		return PARAMETER_NAME_DISCOVERER.getParameterNames(method);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getParameterNames(Constructor constructor) {
		return PARAMETER_NAME_DISCOVERER.getParameterNames(constructor);
	}

	public static ParameterDescriptor[] getParameters(ParameterNameDiscoverer parameterNameDiscoverer,
			Constructor<?> constructor) {
		String[] names = parameterNameDiscoverer.getParameterNames(constructor);
		if (ArrayUtils.isEmpty(names)) {
			return ParameterDescriptor.EMPTY_ARRAY;
		}

		Annotation[][] parameterAnnoatations = constructor.getParameterAnnotations();
		Type[] parameterGenericTypes = constructor.getGenericParameterTypes();
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		ParameterDescriptor[] parameterDefinitions = new ParameterDescriptor[names.length];
		for (int i = 0; i < names.length; i++) {
			AnnotatedElement annotatedElement = MultiAnnotatedElement
					.forAnnotatedElements(AnnotatedElementUtils.forAnnotations(parameterAnnoatations[i]), constructor);
			parameterDefinitions[i] = new DefaultParameterDescriptor(names[i], annotatedElement, parameterTypes[i],
					parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}

	public static ParameterDescriptor[] getParameters(ParameterNameDiscoverer parameterNameDiscoverer, Method method) {
		String[] names = parameterNameDiscoverer.getParameterNames(method);
		if (ArrayUtils.isEmpty(names)) {
			return ParameterDescriptor.EMPTY_ARRAY;
		}

		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ParameterDescriptor[] parameterDefinitions = new ParameterDescriptor[names.length];
		for (int i = 0; i < names.length; i++) {
			AnnotatedElement annotatedElement = MultiAnnotatedElement
					.forAnnotatedElements(AnnotatedElementUtils.forAnnotations(parameterAnnoatations[i]), method);
			parameterDefinitions[i] = new DefaultParameterDescriptor(names[i], annotatedElement, parameterTypes[i],
					parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}
	
	public static ParameterDescriptor[] getParameters(Constructor<?> constructor) {
		return getParameters(PARAMETER_NAME_DISCOVERER, constructor);
	}

	public static ParameterDescriptor[] getParameters(Method method) {
		return getParameters(PARAMETER_NAME_DISCOVERER, method);
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
