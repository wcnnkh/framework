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
	private static LocalVariableTableParameterNameDiscoverer lvtpnd = new LocalVariableTableParameterNameDiscoverer();
	private static ParameterConfigFactory parameterConfigFactory = InstanceUtils
			.autoNewInstanceBySystemProperty(ParameterConfigFactory.class,
					"scw.parameter.config.factory",
					new DefaultParameterConfigFactory());

	private ParameterUtils() {
	};

	public static ParameterConfigFactory getParameterConfigFactory() {
		return parameterConfigFactory;
	}

	public static ParameterConfig[] getParameterConfigs(
			Constructor<?> constructor) {
		return getParameterConfigFactory().getParameterConfigs(constructor);
	}

	public static ParameterConfig[] getParameterConfigs(Method method) {
		return getParameterConfigFactory().getParameterConfigs(method);
	}

	public static String getParameterName(ParameterConfig parameterConfig) {
		ParameterName parameterName = parameterConfig
				.getAnnotation(ParameterName.class);
		if (parameterName != null
				&& StringUtils.isNotEmpty(parameterName.value())) {
			return parameterName.value();
		}
		return parameterConfig.getName();
	}

	public static String[] getParameterName(Method method) {
		return lvtpnd.getParameterNames(method);
	}

	@SuppressWarnings("rawtypes")
	public static String[] getParameterName(Constructor constructor) {
		return lvtpnd.getParameterNames(constructor);
	}

	public static boolean isNullAble(ParameterConfig parameterConfig) {
		if (parameterConfig.getType().isPrimitive()) {
			return false;
		}

		return AnnotationUtils.isNullable(parameterConfig, true);
	}

	public static Object createObjectByParameter(
			ParameterFactory<ParameterConfig> parameterFactory, Class<?> type)
			throws Exception {
		return createObjectByParameter(parameterFactory, type, null);
	}

	public static Object createObjectByParameter(
			InstanceFactory instanceFactory,
			ParameterConfigFactory parameterConfigFactory,
			ParameterFactory<ParameterConfig> parameterFactory, Class<?> type)
			throws Exception {
		return createObjectByParameter(instanceFactory, parameterConfigFactory,
				parameterFactory, type, null);
	}

	public static Object createObjectByParameter(
			ParameterFactory<ParameterConfig> parameterFactory, Class<?> type,
			String name) throws Exception {
		return createObjectByParameterInternal(
				InstanceUtils.REFLECTION_INSTANCE_FACTORY,
				parameterConfigFactory, parameterFactory, type,
				StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name
						: name + "."));
	}

	public static Object createObjectByParameter(
			InstanceFactory instanceFactory,
			ParameterConfigFactory parameterConfigFactory,
			ParameterFactory<ParameterConfig> parameterFactory, Class<?> type,
			String name) throws Exception {
		return createObjectByParameterInternal(instanceFactory,
				parameterConfigFactory, parameterFactory, type,
				StringUtils.isEmpty(name) ? null : (name.endsWith(".") ? name
						: name + "."));
	}

	private static Object createObjectByParameterInternal(
			InstanceFactory instanceFactory,
			ParameterConfigFactory parameterConfigFactory,
			ParameterFactory<ParameterConfig> parameterFactory, Class<?> type,
			String prefix) throws Exception {
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
			ParameterConfigFactory parameterConfigFactory,
			ParameterFactory<ParameterConfig> parameterFactory, Class<?> type,
			String prefix) throws Exception {
		for (FieldParameterConfig parameterConfig : parameterConfigFactory
				.getFieldParameterConfigs(type)) {
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
