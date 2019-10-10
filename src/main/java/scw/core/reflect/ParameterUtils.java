package scw.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.annotation.Require;
import scw.core.utils.ClassUtils;

public final class ParameterUtils {
	private ParameterUtils() {
	};

	public static ParameterConfig[] getParameterConfigs(Constructor<?> constructor) {
		String[] names = ClassUtils.getParameterName(constructor);
		Annotation[][] parameterAnnoatations = constructor.getParameterAnnotations();
		Type[] parameterGenericTypes = constructor.getGenericParameterTypes();
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		ParameterConfig[] parameterDefinitions = new ParameterConfig[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new SimpleParameterConfig(names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}

	public static ParameterConfig[] getParameterConfigs(Method method) {
		String[] names = ClassUtils.getParameterName(method);
		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ParameterConfig[] parameterDefinitions = new ParameterConfig[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new SimpleParameterConfig(names[i], parameterAnnoatations[i],
					parameterTypes[i], parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}

	public static boolean isRequire(ParameterConfig parameterConfig) {
		Require require = parameterConfig.getAnnotation(Require.class);
		if (require != null) {
			return require.value();
		}

		NotRequire notRequire = parameterConfig.getAnnotation(NotRequire.class);
		if (notRequire != null) {
			return !notRequire.value();
		}

		return true;
	}

	public static String getParameterName(ParameterConfig parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotation(ParameterName.class);
		if (parameterName == null) {
			return parameterConfig.getName();
		}

		return parameterName.value();
	}
}
