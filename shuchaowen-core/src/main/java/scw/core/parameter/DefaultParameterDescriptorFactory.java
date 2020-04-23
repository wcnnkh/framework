package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import scw.core.instance.annotation.Configuration;
import scw.core.parameter.field.DefaultFieldDescriptor;
import scw.core.parameter.field.FieldDescriptor;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultParameterDescriptorFactory implements ParameterDescriptorFactory {
	public FieldDescriptor[] getFieldDescriptors(Class<?> clazz) {
		List<FieldDescriptor> parameterConfigs = new LinkedList<FieldDescriptor>();
		Class<?> clz = clazz;
		while (clz != null && clz != Object.class) {
			for (Field field : clz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				ReflectionUtils.setAccessibleField(field);
				parameterConfigs.add(new DefaultFieldDescriptor(field));
			}
			clz = clz.getSuperclass();
		}

		if (parameterConfigs.isEmpty()) {
			return FieldDescriptor.EMPTY_ARRAY;
		}

		return parameterConfigs.toArray(new FieldDescriptor[parameterConfigs.size()]);
	}

	public ParameterDescriptor[] getParameterDescriptors(Constructor<?> constructor) {
		String[] names = ParameterUtils.getParameterName(constructor);
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

	public ParameterDescriptor[] getParameterDescriptors(Method method) {
		String[] names = ParameterUtils.getParameterName(method);
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
}
