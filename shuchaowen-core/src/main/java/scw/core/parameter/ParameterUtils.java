package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import scw.core.annotation.AnnotationUtils;
import scw.core.annotation.ParameterName;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;

public final class ParameterUtils {
	private static final LocalVariableTableParameterNameDiscoverer LVTPND = new LocalVariableTableParameterNameDiscoverer();
	private static final ParameterDescriptorFactory PARAMETER_DESCRIPTOR_FACTORY = InstanceUtils.getSystemConfiguration(ParameterDescriptorFactory.class);

	private ParameterUtils() {
	};

	public static ParameterDescriptorFactory getParameterDescriptorFactory() {
		return PARAMETER_DESCRIPTOR_FACTORY;
	}

	public static ParameterDescriptor[] getParameterDescriptors(
			Constructor<?> constructor) {
		return getParameterDescriptorFactory().getParameterDescriptors(
				constructor);
	}

	public static ParameterDescriptor[] getParameterDescriptors(Method method) {
		return getParameterDescriptorFactory().getParameterDescriptors(method);
	}

	public static String getParameterName(ParameterDescriptor parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotatedElement()
				.getAnnotation(ParameterName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return parameterConfig.getName();
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

		return AnnotationUtils.isNullable(
				parameterConfig.getAnnotatedElement(), true);
	}

	public static Object createObjectByParameter(
			ParameterFactory parameterFactory,
			Class<?> type) throws Exception {
		return createObjectByParameter(parameterFactory, type, null);
	}

	public static Object createObjectByParameter(
			InstanceFactory instanceFactory,
			ParameterDescriptorFactory parameterConfigFactory,
			ParameterFactory parameterFactory,
			Class<?> type) throws Exception {
		return createObjectByParameter(instanceFactory, parameterConfigFactory,
				parameterFactory, type, null);
	}

	public static Object createObjectByParameter(
			ParameterFactory parameterFactory,
			Class<?> type, String name) throws Exception {
		return createObjectByParameterInternal(
				InstanceUtils.INSTANCE_FACTORY,
				getParameterDescriptorFactory(), parameterFactory, type,
				StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name
						: name + "."));
	}

	public static Object createObjectByParameter(
			InstanceFactory instanceFactory,
			ParameterDescriptorFactory parameterDescriptorFactory,
			ParameterFactory parameterFactory,
			Class<?> type, String name) throws Exception {
		return createObjectByParameterInternal(instanceFactory,
				parameterDescriptorFactory, parameterFactory, type,
				StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name
						: name + "."));
	}

	private static Object createObjectByParameterInternal(
			InstanceFactory instanceFactory,
			ParameterDescriptorFactory parameterConfigFactory,
			ParameterFactory parameterFactory,
			Class<?> type, String prefix) throws Exception {
		if (!instanceFactory.isInstance(type)) {
			return null;
		}

		Object obj = instanceFactory.getInstance(type);
		setParameter(obj, instanceFactory, parameterConfigFactory,
				parameterFactory, type, prefix);
		return obj;
	}

	private static void setParameter(Object instance,
			InstanceFactory instanceFactory,
			ParameterDescriptorFactory parameterConfigFactory,
			ParameterFactory parameterFactory,
			Class<?> type, String prefix) throws Exception {
		for (FieldParameterDescriptor parameterConfig : parameterConfigFactory
				.getParameterDescriptors(type)) {
			if (!parameterConfig.getType().isPrimitive()
					&& parameterConfig.getField().get(instance) != null) {
				continue;
			}

			NamePrefixFieldParameterConfig namePrefixFieldParameterConfig = new NamePrefixFieldParameterConfig(
					parameterConfig, prefix);
			Object v = null;
			if (String.class.isAssignableFrom(namePrefixFieldParameterConfig
					.getType())
					|| ClassUtils
							.isPrimitiveOrWrapper(namePrefixFieldParameterConfig
									.getType())) {
				v = parameterFactory
						.getParameter(namePrefixFieldParameterConfig);
			} else {
				v = createObjectByParameterInternal(instanceFactory,
						parameterConfigFactory, parameterFactory, type,
						namePrefixFieldParameterConfig.getName() + ".");
			}

			if (v != null) {
				ReflectionUtils.setFieldValue(type,
						namePrefixFieldParameterConfig.getField(), instance, v);
			}
		}
	}
}
