package scw.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import scw.core.annotation.AnnotationUtils;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.field.DefaultFieldDescriptor;
import scw.core.parameter.field.FieldDescriptor;
import scw.core.parameter.field.NamePrefixFieldDescriptor;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public final class ParameterUtils {
	private static final LocalVariableTableParameterNameDiscoverer LVTPND = new LocalVariableTableParameterNameDiscoverer();

	private ParameterUtils() {
	};

	public static FieldDescriptor[] getFieldDescriptors(Class<?> clazz) {
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

	public static ParameterDescriptor[] getParameterDescriptors(Constructor<?> constructor) {
		String[] names = ParameterUtils.getParameterName(constructor);
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

	public static String[] getParameterName(Method method) {
		return LVTPND.getParameterNames(method);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getParameterName(Constructor constructor) {
		return LVTPND.getParameterNames(constructor);
	}

	public static boolean isNullAble(ParameterDescriptor parameterConfig) {
		if (parameterConfig.getType().isPrimitive()) {
			return false;
		}

		return AnnotationUtils.isNullable(parameterConfig.getAnnotatedElement(), true);
	}

	public static Object createObjectByParameter(ParameterFactory parameterFactory, Class<?> type) throws Exception {
		return createObjectByParameter(parameterFactory, type, null);
	}

	public static Object createObjectByParameter(InstanceFactory instanceFactory, ParameterFactory parameterFactory,
			Class<?> type) throws Exception {
		return createObjectByParameter(instanceFactory, parameterFactory, type, null);
	}

	public static Object createObjectByParameter(ParameterFactory parameterFactory, Class<?> type, String name)
			throws Exception {
		return createObjectByParameterInternal(InstanceUtils.INSTANCE_FACTORY, parameterFactory, type,
				StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
	}

	public static Object createObjectByParameter(InstanceFactory instanceFactory, ParameterFactory parameterFactory,
			Class<?> type, String name) throws Exception {
		return createObjectByParameterInternal(instanceFactory, parameterFactory, type,
				StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name : name + "."));
	}

	private static Object createObjectByParameterInternal(InstanceFactory instanceFactory,
			ParameterFactory parameterFactory, Class<?> type, String prefix) throws Exception {
		if (!instanceFactory.isInstance(type)) {
			return null;
		}

		Object obj = instanceFactory.getInstance(type);
		setParameter(obj, instanceFactory, parameterFactory, type, prefix);
		return obj;
	}

	private static void setParameter(Object instance, InstanceFactory instanceFactory,
			ParameterFactory parameterFactory, Class<?> type, String prefix) throws Exception {
		for (FieldDescriptor parameterConfig : getFieldDescriptors(type)) {
			if (!parameterConfig.getType().isPrimitive() && parameterConfig.getField().get(instance) != null) {
				continue;
			}

			FieldDescriptor fieldDescriptor = new NamePrefixFieldDescriptor(parameterConfig, prefix);
			Object v = null;
			if (String.class.isAssignableFrom(fieldDescriptor.getType())
					|| ClassUtils.isPrimitiveOrWrapper(fieldDescriptor.getType())) {
				v = parameterFactory.getParameter(fieldDescriptor);
			} else {
				v = createObjectByParameterInternal(instanceFactory, parameterFactory, type,
						fieldDescriptor.getDisplayName() + ".");
			}

			if (v != null) {
				ReflectionUtils.setFieldValue(type, fieldDescriptor.getField(), instance, v);
			}
		}
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
}
