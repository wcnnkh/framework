package scw.core.instance;

import java.lang.reflect.Constructor;
import java.util.Collection;

import scw.core.PropertyFactory;
import scw.core.parameter.ParameterConfig;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.util.value.StringParseValueFactory;
import scw.util.value.Value;
import scw.util.value.ValueFactory;

public class AutoInstanceConfig implements InstanceConfig {
	protected final InstanceFactory instanceFactory;
	protected final PropertyFactory propertyFactory;
	protected final Class<?> clazz;
	protected final ValueFactory<String, ? extends Value> valueFactory;
	private Constructor<?> constructor;
	private ParameterConfig[] parameterConfigs;

	public AutoInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz) {
		this(instanceFactory, propertyFactory, StringParseValueFactory.STRING_PARSE_VALUE_FACTORY, clazz,
				ReflectionUtils.getConstructorOrderList(clazz));
	}

	public AutoInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory,
			ValueFactory<String, ? extends Value> valueFactory, Class<?> clazz,
			Collection<Constructor<?>> constructors) {
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

	public final boolean isAutoConstructor(Constructor<?> constructor) {
		return InstanceUtils.isAuto(instanceFactory, propertyFactory, valueFactory, clazz,
				ParameterUtils.getParameterConfigs(constructor), constructor);
	}

	public final Constructor<?> getConstructor() {
		return constructor;
	}

	public final Object[] getArgs() {
		if (constructor == null) {
			return null;
		}

		return InstanceUtils.getAutoArgs(instanceFactory, propertyFactory, valueFactory, clazz, parameterConfigs);
	}
}
