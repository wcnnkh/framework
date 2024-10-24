package io.basc.framework.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import io.basc.framework.core.DefaultParameterNameDiscoverer;
import io.basc.framework.core.ParameterNameDiscoverer;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;

public final class ParameterUtils {
	private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

	private ParameterUtils() {
	};

	public static DefaultParameterNameDiscoverer getParameterNameDiscoverer() {
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
		throw Assert.shouldNeverGetHere();
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
			parameterDefinitions[i] = new DefaultParameterDescriptor(names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i]);
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

	public static boolean isisAssignable(ParameterDescriptors parameterDescriptors, Class<?>[] types) {
		// 异或运算，如果两个不同则结果为1
		if (parameterDescriptors.size() == 0 ^ ArrayUtils.isEmpty(types)) {
			return false;
		}

		return ClassUtils.isAssignable(Arrays.asList(parameterDescriptors.getTypes()), Arrays.asList(types));
	}

	public static <T> Object invoke(Class<T> type, Object instance, String name, Map<String, Object> parameterMap)
			throws NoSuchMethodException {
		if (CollectionUtils.isEmpty(parameterMap)) {
			try {
				return ReflectionUtils.getDeclaredMethod(type, name).invoke(instance);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}

		int size = parameterMap.size();
		Iterator<Method> iterator = ReflectionUtils.getDeclaredMethods(type).withAll().all().stream().iterator();
		while (iterator.hasNext()) {
			Method method = iterator.next();
			if (size == method.getParameterTypes().length) {
				String[] names = ParameterUtils.getParameterNames(method);
				Object[] args = new Object[size];
				boolean find = true;
				for (int i = 0; i < names.length; i++) {
					if (!parameterMap.containsKey(names[i])) {
						find = false;
						break;
					}

					args[i] = parameterMap.get(names[i]);
				}

				if (find) {
					return ReflectionUtils.invoke(method, instance, args);
				}
			}
		}
		throw new NoSuchMethodException(type.getName() + ", method=" + name);
	}
}
