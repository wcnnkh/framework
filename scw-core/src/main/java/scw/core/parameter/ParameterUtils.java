package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;

import scw.core.parameter.annotation.DefaultValue;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.util.JavaVersion;
import scw.value.StringValue;
import scw.value.Value;

public final class ParameterUtils {
	private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER;

	static {
		ParameterNameDiscoverer parameterNameDiscoverer = null;
		if (JavaVersion.INSTANCE.getMasterVersion() >= 8) {
			try {
				parameterNameDiscoverer = (ParameterNameDiscoverer) ClassUtils
						.forName("scw.core.parameter.Jdk8ParameterNameDiscoverer", null).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
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
			parameterDefinitions[i] = new DefaultParameterDescriptor(names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i]);
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
			parameterDefinitions[i] = new DefaultParameterDescriptor(names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i]);
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

	public static Value getDefaultValue(ParameterDescriptor parameterDescriptor) {
		DefaultValue defaultValue = parameterDescriptor.getAnnotatedElement().getAnnotation(DefaultValue.class);
		if (defaultValue == null) {
			return null;
		}
		return new StringValue(defaultValue.value());
	}

	public static String getDisplayName(ParameterDescriptor parameterDescriptor) {
		ParameterName parameterName = parameterDescriptor.getAnnotatedElement().getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return parameterDescriptor.getName();
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
	
	public static boolean isisAssignable(ParameterDescriptors parameterDescriptors, Class<?>[] types){
		// 异或运算，如果两个不同则结果为1
		if (parameterDescriptors.size() == 0 ^ ArrayUtils.isEmpty(types)) {
			return false;
		}

		return ClassUtils.isAssignable(Arrays.asList(parameterDescriptors.getTypes()), Arrays.asList(types));
	}
}
