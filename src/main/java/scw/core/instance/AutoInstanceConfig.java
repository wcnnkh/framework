package scw.core.instance;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import scw.core.PropertyFactory;
import scw.core.ValueFactory;
import scw.core.annotation.ParameterName;
import scw.core.annotation.DefaultValue;
import scw.core.instance.annotation.PropertyParameter;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.ContainAnnotationParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.resource.ResourceUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParse;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class AutoInstanceConfig implements InstanceConfig {
	private static Logger logger = LoggerUtils.getLogger(AutoInstanceConfig.class);
	protected final InstanceFactory instanceFactory;
	protected final PropertyFactory propertyFactory;
	protected final Class<?> clazz;
	protected final ValueFactory<String> valueFactory;
	private Constructor<?> constructor;
	private ContainAnnotationParameterConfig[] containAnnotationParameterConfigs;

	public AutoInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz) {
		this(instanceFactory, propertyFactory, StringParse.DEFAULT, clazz, ReflectUtils.getConstructorOrderList(clazz));
	}

	public AutoInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ValueFactory<String> valueFactory, Class<?> clazz, Collection<Constructor<?>> constructors) {
		this.propertyFactory = propertyFactory;
		this.instanceFactory = instanceFactory;
		this.clazz = clazz;
		this.valueFactory = valueFactory;
		for (Constructor<?> constructor : constructors) {
			if (isAutoConstructor(constructor)) {
				this.constructor = constructor;
				this.containAnnotationParameterConfigs = ParameterUtils.getParameterConfigs(constructor);
			}
		}
	}

	protected String getDefaultName(ContainAnnotationParameterConfig containAnnotationParameterConfig) {
		return clazz.getClass().getName() + "." + containAnnotationParameterConfig.getName();
	}

	protected String getProperty(ContainAnnotationParameterConfig containAnnotationParameterConfig) {
		ParameterName parameterName = containAnnotationParameterConfig.getAnnotation(ParameterName.class);
		String value = propertyFactory.getProperty(
				parameterName == null ? getDefaultName(containAnnotationParameterConfig) : parameterName.value());
		if (value == null) {
			DefaultValue defaultValue = containAnnotationParameterConfig.getAnnotation(DefaultValue.class);
			if (defaultValue != null) {
				value = defaultValue.value();
			}
		}

		ResourceParameter resourceParameter = containAnnotationParameterConfig.getAnnotation(ResourceParameter.class);
		if (resourceParameter != null) {
			if (StringUtils.isEmpty(value)) {
				boolean b = StringUtils.isEmpty(resourceParameter.value()) ? false
						: ResourceUtils.isExist(resourceParameter.value());
				value = b ? resourceParameter.value() : null;
			} else {
				if (!ResourceUtils.isExist(value)) {
					boolean b = StringUtils.isEmpty(resourceParameter.value()) ? false
							: ResourceUtils.isExist(resourceParameter.value());
					value = b ? resourceParameter.value() : null;
				}
			}
		}
		return value;
	}

	protected boolean isProerptyType(ContainAnnotationParameterConfig containAnnotationParameterConfig) {
		PropertyParameter propertyParameter = containAnnotationParameterConfig.getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = containAnnotationParameterConfig.getType();
			return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type.isArray() || type.isEnum()
					|| Class.class == type || BigDecimal.class == type || BigInteger.class == type
					|| Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
		} else {
			return propertyParameter.value();
		}
	}

	protected String getInstanceName(ContainAnnotationParameterConfig containAnnotationParameterConfig) {
		ParameterName parameterName = containAnnotationParameterConfig.getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			String value = propertyFactory.getProperty(parameterName.value());
			if (value == null) {
				return null;
			}

			return instanceFactory.isInstance(value) ? null : value;
		} else {
			if (instanceFactory.isInstance(containAnnotationParameterConfig.getType())) {
				return containAnnotationParameterConfig.getType().getName();
			}

			String name = getDefaultName(containAnnotationParameterConfig);
			if (instanceFactory.isInstance(name)) {
				return name;
			}

			return null;
		}
	}

	public final boolean isAutoConstructor(Constructor<?> constructor) {
		ContainAnnotationParameterConfig[] parameterDefinitions = ParameterUtils.getParameterConfigs(constructor);
		if (parameterDefinitions.length == 0) {
			return true;
		}

		for (int i = 0; i < parameterDefinitions.length; i++) {
			ContainAnnotationParameterConfig containAnnotationParameterConfig = parameterDefinitions[i];
			boolean require = ParameterUtils.isRequire(containAnnotationParameterConfig);
			if (!require) {
				continue;
			}

			boolean isProperty = isProerptyType(containAnnotationParameterConfig);
			if (logger.isDebugEnabled()) {
				logger.debug("{} parameter index {} is {}", constructor, i, isProperty ? "property" : "bean");
			}

			// 是否是属性而不是bean
			if (isProperty) {
				String value = getProperty(containAnnotationParameterConfig);
				if (StringUtils.isEmpty(value)) {
					return false;
				}
			} else {
				String name = getInstanceName(containAnnotationParameterConfig);
				if (name == null) {
					return false;
				}
			}
		}
		return true;
	}

	public final Constructor<?> getConstructor() {
		return constructor;
	}

	public final Object[] getArgs() {
		if (constructor == null) {
			return null;
		}

		if (containAnnotationParameterConfigs.length == 0) {
			return new Object[0];
		}

		Object[] args = new Object[containAnnotationParameterConfigs.length];
		for (int i = 0; i < containAnnotationParameterConfigs.length; i++) {
			ContainAnnotationParameterConfig containAnnotationParameterConfig = containAnnotationParameterConfigs[i];
			boolean require = ParameterUtils.isRequire(containAnnotationParameterConfig);
			if (isProerptyType(containAnnotationParameterConfig)) {
				String value = getProperty(containAnnotationParameterConfig);
				if (require && StringUtils.isEmpty(value)) {
					return null;
				}

				args[i] = XUtils.getValue(valueFactory, value, containAnnotationParameterConfig.getGenericType());
			} else {
				String name = getInstanceName(containAnnotationParameterConfig);
				args[i] = name == null ? null : instanceFactory.getInstance(name);
			}
		}
		return args;
	}
}
