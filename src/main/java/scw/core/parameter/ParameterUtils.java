package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import scw.core.annotation.NotRequire;
import scw.core.annotation.ParameterName;
import scw.core.annotation.Require;
import scw.core.reflect.SimpleParameterConfig;

public final class ParameterUtils {
	private static LocalVariableTableParameterNameDiscoverer lvtpnd = new LocalVariableTableParameterNameDiscoverer();
	private ParameterUtils() {
	};

	public static ParameterConfig[] getParameterConfigs(Constructor<?> constructor) {
		String[] names = getParameterName(constructor);
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
		String[] names = getParameterName(method);
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

	public static boolean isRequire(ParameterConfig containAnnotationParameterConfig) {
		Require require = containAnnotationParameterConfig.getAnnotation(Require.class);
		if (require != null) {
			return require.value();
		}

		NotRequire notRequire = containAnnotationParameterConfig.getAnnotation(NotRequire.class);
		if (notRequire != null) {
			return !notRequire.value();
		}

		return true;
	}

	public static String getParameterName(ParameterConfig containAnnotationParameterConfig) {
		ParameterName parameterName = containAnnotationParameterConfig.getAnnotation(ParameterName.class);
		if (parameterName == null) {
			return containAnnotationParameterConfig.getName();
		}

		return parameterName.value();
	}
	
	public static String[] getParameterName(Method method) {
		return lvtpnd.getParameterNames(method);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getParameterName(Constructor constructor) {
		return lvtpnd.getParameterNames(constructor);
	}
}
