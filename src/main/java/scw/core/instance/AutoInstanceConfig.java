package scw.core.instance;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

import scw.core.PropertyFactory;
import scw.core.ValueFactory;
import scw.core.annotation.DefaultValue;
import scw.core.annotation.ParameterName;
import scw.core.instance.annotation.PropertyParameter;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.ParameterConfig;
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
	private ParameterConfig[] parameterConfigs;

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
				this.parameterConfigs = ParameterUtils.getParameterConfigs(constructor);
				break;
			}
		}
	}

	protected String getDefaultName(ParameterConfig parameterConfig) {
		return clazz.getClass().getName() + "." + parameterConfig.getName();
	}

	protected String getProperty(ParameterConfig parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotation(ParameterName.class);
		String value = propertyFactory
				.getProperty(parameterName == null ? getDefaultName(parameterConfig) : parameterName.value());
		if (value == null) {
			DefaultValue defaultValue = parameterConfig.getAnnotation(DefaultValue.class);
			if (defaultValue != null) {
				value = defaultValue.value();
			}
		}
		
		if(value != null){
			ResourceParameter resourceParameter = parameterConfig.getAnnotation(ResourceParameter.class);
			if (resourceParameter != null) {
				if (!ResourceUtils.isExist(value)) {
					return null;
				}
			}
		}
		return value;
	}

	protected boolean isProerptyType(ParameterConfig parameterConfig) {
		PropertyParameter propertyParameter = parameterConfig.getAnnotation(PropertyParameter.class);
		if (propertyParameter == null) {
			Class<?> type = parameterConfig.getType();
			return ClassUtils.isPrimitiveOrWrapper(type) || type == String.class || type.isArray() || type.isEnum()
					|| Class.class == type || BigDecimal.class == type || BigInteger.class == type
					|| Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
		} else {
			return propertyParameter.value();
		}
	}

	protected String getInstanceName(ParameterConfig parameterConfig) {
		ParameterName parameterName = parameterConfig.getAnnotation(ParameterName.class);
		if (parameterName != null && StringUtils.isNotEmpty(parameterName.value())) {
			String value = propertyFactory.getProperty(parameterName.value());
			if (value == null) {
				return null;
			}

			return instanceFactory.isInstance(value) ? null : value;
		} else {
			if (instanceFactory.isInstance(parameterConfig.getType())) {
				return parameterConfig.getType().getName();
			}

			String name = getDefaultName(parameterConfig);
			if (instanceFactory.isInstance(name)) {
				return name;
			}

			return null;
		}
	}

	public final boolean isAutoConstructor(Constructor<?> constructor) {
		ParameterConfig[] parameterDefinitions = ParameterUtils.getParameterConfigs(constructor);
		if (parameterDefinitions.length == 0) {
			return true;
		}

		for (int i = 0; i < parameterDefinitions.length; i++) {
			ParameterConfig parameterConfig = parameterDefinitions[i];
			boolean require = ParameterUtils.isRequire(parameterConfig);
			if (!require) {
				continue;
			}

			boolean isProperty = isProerptyType(parameterConfig);
			// 是否是属性而不是bean
			boolean b = true;
			if (isProperty) {
				String value = getProperty(parameterConfig);
				if (StringUtils.isEmpty(value)) {
					b = false;
				}
			} else {
				String name = getInstanceName(parameterConfig);
				if (name == null) {
					b = false;
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("{} parameter index {} is {} matching:{}", constructor, i,
						isProperty ? "property" : "bean", b ? "success" : "fail");
			}

			if (!b) {
				return false;
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

		if (parameterConfigs.length == 0) {
			return new Object[0];
		}

		Object[] args = new Object[parameterConfigs.length];
		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterConfig parameterConfig = parameterConfigs[i];
			boolean require = ParameterUtils.isRequire(parameterConfig);
			if (isProerptyType(parameterConfig)) {
				String value = getProperty(parameterConfig);
				if (require && StringUtils.isEmpty(value)) {
					return null;
				}

				args[i] = XUtils.getValue(valueFactory, value, parameterConfig.getGenericType());
			} else {
				String name = getInstanceName(parameterConfig);
				args[i] = name == null ? null : instanceFactory.getInstance(name);
			}
		}
		return args;
	}
}
