package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import scw.core.reflect.ReflectionUtils;

public class DefaultParameterConfigFactory implements ParameterConfigFactory {

	public FieldParameterConfig[] getFieldParameterConfigs(Class<?> clazz) {
		List<FieldParameterConfig> parameterConfigs = new LinkedList<FieldParameterConfig>();
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				ReflectionUtils.setAccessibleField(field);
				parameterConfigs.add(new FieldParameterConfig(field));
			}
			clz = clz.getSuperclass();
		}
		return parameterConfigs
				.toArray(new FieldParameterConfig[parameterConfigs.size()]);
	}

	public ParameterConfig[] getParameterConfigs(Constructor<?> constructor) {
		String[] names = ParameterUtils.getParameterName(constructor);
		Annotation[][] parameterAnnoatations = constructor
				.getParameterAnnotations();
		Type[] parameterGenericTypes = constructor.getGenericParameterTypes();
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		ParameterConfig[] parameterDefinitions = new ParameterConfig[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new DefaultParameterConfig(names[i],
					parameterAnnoatations[i], parameterTypes[i],
					parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}

	public ParameterConfig[] getParameterConfigs(Method method) {
		String[] names = ParameterUtils.getParameterName(method);
		Annotation[][] parameterAnnoatations = method.getParameterAnnotations();
		Type[] parameterGenericTypes = method.getGenericParameterTypes();
		Class<?>[] parameterTypes = method.getParameterTypes();
		ParameterConfig[] parameterDefinitions = new ParameterConfig[names.length];
		for (int i = 0; i < names.length; i++) {
			parameterDefinitions[i] = new DefaultParameterConfig(names[i],
					parameterAnnoatations[i], parameterTypes[i],
					parameterGenericTypes[i]);
		}
		return parameterDefinitions;
	}
}
