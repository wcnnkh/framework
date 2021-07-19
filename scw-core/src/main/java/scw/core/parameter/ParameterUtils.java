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
	private static final ParameterDescriptorsResolver PARAMETER_DESCRIPTORS_RESOLVER;

	static {
		ParameterNameDiscoverer parameterNameDiscoverer = null;
		if (JavaVersion.INSTANCE.getMasterVersion() >= 8) {
			parameterNameDiscoverer = ClassUtils.newInstance("scw.core.parameter.Jdk8ParameterNameDiscoverer", null);
		} else {
			parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		}

		PARAMETER_NAME_DISCOVERER = parameterNameDiscoverer == null ? new LocalVariableTableParameterNameDiscoverer()
				: parameterNameDiscoverer;
		
		PARAMETER_DESCRIPTORS_RESOLVER = new DefaultParameterDescriptorsResolver(PARAMETER_NAME_DISCOVERER);
	}

	private ParameterUtils() {
	};

	public static ParameterNameDiscoverer getParameterNameDiscoverer() {
		return PARAMETER_NAME_DISCOVERER;
	}

	public static ParameterDescriptorsResolver getParameterDescriptorsResolver() {
		return PARAMETER_DESCRIPTORS_RESOLVER;
	}

	public static ParameterDescriptor[] getParameterDescriptors(Constructor<?> constructor) {
		String[] names = ParameterUtils.getParameterNames(constructor);
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

	public static ParameterDescriptor[] getParameterDescriptors(Method method) {
		String[] names = ParameterUtils.getParameterNames(method);
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

	public static String[] getParameterNames(Method method) {
		return getParameterNameDiscoverer().getParameterNames(method);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getParameterNames(Constructor constructor) {
		return getParameterNameDiscoverer().getParameterNames(constructor);
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
		return getParameterMap(getParameterDescriptors(method), args);
	}

	public static LinkedHashMap<String, Object> getParameterMap(Constructor<?> constructor, Object[] args) {
		return getParameterMap(getParameterDescriptors(constructor), args);
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
